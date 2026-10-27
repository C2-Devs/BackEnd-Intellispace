package com.intellispace.backend.catalog.application.service;

import com.intellispace.backend.catalog.adapter.in.web.CustomFurnitureResponse;
import com.intellispace.backend.catalog.adapter.in.web.CustomFurnitureResponse.Dimensions;
import com.intellispace.backend.catalog.adapter.out.persistence.CatalogItemEntity;
import com.intellispace.backend.catalog.adapter.out.persistence.CatalogItemJpaRepository;
import com.intellispace.backend.storage.SupabaseStorageClient;
import com.intellispace.backend.workspace.application.port.out.WorkspaceRepository;
import com.intellispace.backend.workspace.domain.Workspace;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomFurnitureService {
    private static final long MAX_MODEL_BYTES = 50L * 1024 * 1024;
    private static final long MAX_THUMBNAIL_BYTES = 5L * 1024 * 1024;

    private static final Set<String> CATEGORIES = Set.of(
            "living-room", "bedroom", "office", "kitchen", "bathroom",
            "plants", "chairs", "tables", "storage", "lighting", "decor");

    private final WorkspaceRepository workspaceRepository;
    private final CatalogItemJpaRepository catalogRepository;
    private final SupabaseStorageClient storage;

    public CustomFurnitureService(
            WorkspaceRepository workspaceRepository,
            CatalogItemJpaRepository catalogRepository,
            SupabaseStorageClient storage) {
        this.workspaceRepository = workspaceRepository;
        this.catalogRepository = catalogRepository;
        this.storage = storage;
    }

    public List<CustomFurnitureResponse> list(UUID userId, UUID workspaceId) {
        requireOwnedWorkspace(userId, workspaceId);

        return catalogRepository
                .findAllByWorkspaceId(
                        workspaceId,
                        Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomFurnitureResponse upload(
            UUID userId,
            UUID workspaceId,
            String name,
            String category,
            String description,
            MultipartFile model,
            MultipartFile thumbnail) {

        requireOwnedWorkspace(userId, workspaceId);

        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isBlank() || cleanName.length() > 150) {
            throw badRequest(
                    "Name is required and must be at most 150 characters.");
        }

        String cleanCategory = category == null
                ? ""
                : category.trim().toLowerCase(Locale.ROOT);

        if (CATEGORIES.contains(cleanCategory) == false) {
            throw badRequest("Choose a valid furniture category.");
        }

        String cleanDescription = description == null ? "" : description.trim();
        if (cleanDescription.length() > 2000) {
            throw badRequest("Description must be at most 2000 characters.");
        }

        byte[] modelBytes = readAndValidateModel(model);
        byte[] thumbnailBytes = readBytes(thumbnail, "thumbnail");
        String thumbnailType = detectThumbnailType(thumbnail, thumbnailBytes);

        UUID assetId = UUID.randomUUID();
        String modelPath = workspaceId + "/" + assetId + "/model.glb";
        String extension = switch (thumbnailType) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            default -> "webp";
        };
        String thumbnailPath =
                workspaceId + "/" + assetId + "/thumbnail." + extension;

        boolean modelUploaded = false;
        boolean thumbnailUploaded = false;

        try {
            storage.upload(modelPath, modelBytes, "model/gltf-binary");
            modelUploaded = true;

            storage.upload(thumbnailPath, thumbnailBytes, thumbnailType);
            thumbnailUploaded = true;

            CatalogItemEntity saved = catalogRepository.save(
                    CatalogItemEntity.builder()
                            .slug("custom-" + assetId)
                            .name(cleanName)
                            .category(cleanCategory)
                            .description(cleanDescription.isBlank()
                                    ? null
                                    : cleanDescription)
                            .modelPath(modelPath)
                            .thumbnailPath(thumbnailPath)
                            .workspaceId(workspaceId)
                            .baseWidth(1.0)
                            .baseDepth(1.0)
                            .baseHeight(1.0)
                            .build());

            return toResponse(saved);
        } catch (RuntimeException error) {
            if (thumbnailUploaded) {
                storage.deleteQuietly(thumbnailPath);
            }
            if (modelUploaded) {
                storage.deleteQuietly(modelPath);
            }
            throw error;
        }
    }

    private Workspace requireOwnedWorkspace(UUID userId, UUID workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Workspace not found."));

        if (userId == null || userId.equals(workspace.getOwnerId()) == false) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Workspace not found.");
        }

        return workspace;
    }

    private CustomFurnitureResponse toResponse(CatalogItemEntity item) {
        return new CustomFurnitureResponse(
                item.getId(),
                item.getWorkspaceId(),
                item.getSlug(),
                item.getName(),
                item.getCategory(),
                item.getDescription(),
                storage.createSignedUrl(item.getModelPath()),
                storage.createSignedUrl(item.getThumbnailPath()),
                new Dimensions(
                        numberOrOne(item.getBaseWidth()),
                        numberOrOne(item.getBaseHeight()),
                        numberOrOne(item.getBaseDepth())),
                List.of(
                        item.getDefaultScaleX(),
                        item.getDefaultScaleY(),
                        item.getDefaultScaleZ()),
                List.of(
                        item.getDefaultRotX(),
                        item.getDefaultRotY(),
                        item.getDefaultRotZ()),
                item.getSnapHeight(),
                true);
    }

    private byte[] readAndValidateModel(MultipartFile model) {
        if (model == null || model.isEmpty()) {
            throw badRequest("Choose a .glb model file.");
        }

        if (model.getSize() > MAX_MODEL_BYTES) {
            throw badRequest("The model must be 50 MB or smaller.");
        }

        String filename = model.getOriginalFilename();
        if (filename == null
                || filename.toLowerCase(Locale.ROOT).endsWith(".glb") == false) {
            throw badRequest("The model must use the .glb extension.");
        }

        byte[] bytes = readBytes(model, "model");
        if (bytes.length < 12
                || "glTF".equals(new String(
                        bytes, 0, 4, StandardCharsets.US_ASCII)) == false) {
            throw badRequest("The model is not a valid binary glTF file.");
        }

        ByteBuffer header = ByteBuffer.wrap(bytes)
                .order(ByteOrder.LITTLE_ENDIAN);

        int version = header.getInt(4);
        long declaredLength = Integer.toUnsignedLong(header.getInt(8));

        if (version != 2 || declaredLength != bytes.length) {
            throw badRequest("The model must be a valid GLB 2.0 file.");
        }

        return bytes;
    }

    private String detectThumbnailType(
            MultipartFile thumbnail, byte[] bytes) {

        if (thumbnail == null || thumbnail.isEmpty()) {
            throw badRequest("Choose a thumbnail image.");
        }

        if (thumbnail.getSize() > MAX_THUMBNAIL_BYTES) {
            throw badRequest("The thumbnail must be 5 MB or smaller.");
        }

        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4e
                && bytes[3] == 0x47
                && bytes[4] == 0x0d
                && bytes[5] == 0x0a
                && bytes[6] == 0x1a
                && bytes[7] == 0x0a) {
            return "image/png";
        }

        if (bytes.length >= 3
                && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8
                && (bytes[2] & 0xff) == 0xff) {
            return "image/jpeg";
        }

        if (bytes.length >= 12
                && "RIFF".equals(new String(
                        bytes, 0, 4, StandardCharsets.US_ASCII))
                && "WEBP".equals(new String(
                        bytes, 8, 4, StandardCharsets.US_ASCII))) {
            return "image/webp";
        }

        throw badRequest("Thumbnail must be a PNG, JPEG, or WebP image.");
    }

    private byte[] readBytes(MultipartFile file, String label) {
        if (file == null || file.isEmpty()) {
            throw badRequest("Choose a " + label + " file.");
        }

        try {
            return file.getBytes();
        } catch (Exception error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Could not read uploaded " + label + " file.");
        }
    }

    private double numberOrOne(Double value) {
        return value == null || Double.isFinite(value) == false || value <= 0
                ? 1.0
                : value;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
