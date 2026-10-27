package com.intellispace.backend.catalog.adapter.in.web;

import com.intellispace.backend.catalog.application.service.CustomFurnitureService;
import com.intellispace.backend.common.security.CurrentUserId;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/custom-furniture")
public class CustomFurnitureController {
    private final CustomFurnitureService service;

    public CustomFurnitureController(CustomFurnitureService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomFurnitureResponse> list(
            @PathVariable UUID workspaceId,
            @CurrentUserId UUID userId) {
        return service.list(userId, workspaceId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomFurnitureResponse upload(
            @PathVariable UUID workspaceId,
            @CurrentUserId UUID userId,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("model") MultipartFile model,
            @RequestParam("thumbnail") MultipartFile thumbnail) {
        return service.upload(
                userId, workspaceId, name, category, description, model, thumbnail);
    }
}
