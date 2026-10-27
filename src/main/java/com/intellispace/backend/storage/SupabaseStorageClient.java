package com.intellispace.backend.storage;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SupabaseStorageClient {
    private static final int SIGNED_URL_TTL_SECONDS = 3600;

    private final RestClient restClient = RestClient.create();

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.secret-key:${supabase.service-role-key:}}")
    private String secretKey;

    @Value("${supabase.storage.bucket:custom-furniture}")
    private String bucket;

    public void upload(String objectPath, byte[] bytes, String contentType) {
        requireConfigured();

        var request = restClient.post()
                .uri(storageBase() + "/object/" + bucket + "/" + safePath(objectPath))
                .header("apikey", secretKey);

        addAuthorizationHeader(request);

        request.header("x-upsert", "false")
                .contentType(MediaType.parseMediaType(contentType))
                .body(bytes)
                .retrieve()
                .toBodilessEntity();
    }

    public String createSignedUrl(String objectPath) {
        requireConfigured();

        var request = restClient.post()
                .uri(storageBase() + "/object/sign/" + bucket + "/" + safePath(objectPath))
                .header("apikey", secretKey);

        addAuthorizationHeader(request);

        JsonNode payload = request
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", SIGNED_URL_TTL_SECONDS))
                .retrieve()
                .body(JsonNode.class);

        String signedPath = payload == null
                ? ""
                : payload.path("signedURL").asText();

        if (signedPath.isBlank() && payload != null) {
            signedPath = payload.path("signedUrl").asText();
        }

        if (signedPath.isBlank()) {
            throw new IllegalStateException(
                    "Supabase Storage did not return a signed URL.");
        }

        if (signedPath.startsWith("https://")
                || signedPath.startsWith("http://")) {
            return signedPath;
        }

        if (signedPath.startsWith("/storage/v1/")) {
            return baseUrl() + signedPath;
        }

        return baseUrl() + "/storage/v1"
                + (signedPath.startsWith("/") ? signedPath : "/" + signedPath);
    }

    public void deleteQuietly(String objectPath) {
        if (isConfigured() == false
                || objectPath == null
                || objectPath.isBlank()) {
            return;
        }

        try {
            var request = restClient.delete()
                    .uri(storageBase() + "/object/" + bucket + "/" + safePath(objectPath))
                    .header("apikey", secretKey);

            addAuthorizationHeader(request);
            request.retrieve().toBodilessEntity();
        } catch (RuntimeException ignored) {
            // Storage cleanup must not hide the original error.
        }
    }

    private void addAuthorizationHeader(RestClient.RequestHeadersSpec<?> request) {
        // Older service-role keys are JWTs and use both headers.
        // New sb_secret keys are sent as apikey only.
        if (secretKey.startsWith("eyJ")) {
            request.header("Authorization", "Bearer " + secretKey);
        }
    }

    private void requireConfigured() {
        if (isConfigured() == false) {
            throw new IllegalStateException(
                    "Custom furniture storage is not configured. "
                    + "Set SUPABASE_URL and SUPABASE_SECRET_KEY.");
        }

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException(
                    "SUPABASE_STORAGE_BUCKET must not be blank.");
        }
    }

    private boolean isConfigured() {
        return supabaseUrl != null
                && supabaseUrl.isBlank() == false
                && secretKey != null
                && secretKey.isBlank() == false;
    }

    private String baseUrl() {
        return supabaseUrl.replaceAll("/+$", "");
    }

    private String storageBase() {
        return baseUrl() + "/storage/v1";
    }

    private String safePath(String path) {
        if (path == null
                || path.isBlank()
                || path.startsWith("/")
                || path.contains("..")
                || path.contains("\\")) {
            throw new IllegalArgumentException("Invalid storage object path.");
        }
        return path;
    }
}
