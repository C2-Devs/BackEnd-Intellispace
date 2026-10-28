package com.intellispace.backend.storage;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SupabaseStorageClient {
    private static final int SIGNED_URL_TTL_SECONDS = 3600;

    private static final long SIGNED_URL_CACHE_TTL_MILLIS = Duration.ofMinutes(55).toMillis();
    private static final long SIGNED_URL_REFRESH_SKEW_MILLIS = Duration.ofSeconds(30).toMillis();

    private final RestClient restClient = RestClient.create();
    private final RestClient signingClient;
    private final ConcurrentMap<String, CachedSignedUrl> signedUrlCache = new ConcurrentHashMap<>();
    private final Semaphore signingPermits = new Semaphore(8);

    private record CachedSignedUrl(String url, long expiresAtMillis) {}

    public SupabaseStorageClient() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(Duration.ofSeconds(12));
        this.signingClient = RestClient.builder().requestFactory(factory).build();
    }

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
        String path = safePath(objectPath);

        CachedSignedUrl cached = signedUrlCache.compute(path, (key, current) -> {
            long now = System.currentTimeMillis();
            if (current != null
                    && current.expiresAtMillis() > now + SIGNED_URL_REFRESH_SKEW_MILLIS) {
                return current;
            }
            return new CachedSignedUrl(requestSignedUrl(key),
                    now + SIGNED_URL_CACHE_TTL_MILLIS);
        });

        if (signedUrlCache.size() > 10000) {
            long now = System.currentTimeMillis();
            signedUrlCache.entrySet().removeIf(
                    entry -> entry.getValue().expiresAtMillis() <= now);
        }

        return cached.url();
    }

    private String requestSignedUrl(String objectPath) {
        boolean acquired = false;
        try {
            signingPermits.acquire();
            acquired = true;

            var request = signingClient.post()
                    .uri(storageBase() + "/object/sign/" + bucket + "/" + objectPath)
                    .header("apikey", secretKey);
            addAuthorizationHeader(request);

            JsonNode payload = request
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("expiresIn", SIGNED_URL_TTL_SECONDS))
                    .retrieve()
                    .body(JsonNode.class);

            String signedPath = payload == null ? "" : payload.path("signedURL").asText();
            if (signedPath.isBlank() && payload != null) {
                signedPath = payload.path("signedUrl").asText();
            }
            if (signedPath.isBlank()) {
                throw new IllegalStateException(
                        "Supabase Storage did not return a signed URL.");
            }
            if (signedPath.startsWith("https://") || signedPath.startsWith("http://")) {
                return signedPath;
            }
            if (signedPath.startsWith("/storage/v1/")) return baseUrl() + signedPath;
            return baseUrl() + "/storage/v1"
                    + (signedPath.startsWith("/") ? signedPath : "/" + signedPath);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while generating a signed storage URL.", error);
        } finally {
            if (acquired) signingPermits.release();
        }
    }

    public void deleteQuietly(String objectPath) {
        if (objectPath != null) signedUrlCache.remove(objectPath);
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
