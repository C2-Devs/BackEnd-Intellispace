package com.intellispace.backend.catalog.adapter.in.web;

import com.intellispace.backend.catalog.application.port.in.CatalogItemQueryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogItemQueryUseCase catalogItemQueryUseCase;

    public CatalogController(CatalogItemQueryUseCase catalogItemQueryUseCase) {
        this.catalogItemQueryUseCase = catalogItemQueryUseCase;
    }

    @GetMapping
    public List<CatalogItemResponse> listCatalogItems() {
        return catalogItemQueryUseCase.listCatalogItems()
                .stream()
                .map(CatalogItemResponse::from)
                .toList();
    }
}
