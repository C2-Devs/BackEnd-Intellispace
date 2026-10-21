package com.intellispace.backend.catalog.application.service;

import com.intellispace.backend.catalog.adapter.out.persistence.CatalogItemJpaRepository;
import com.intellispace.backend.catalog.application.port.in.CatalogItemQueryUseCase;
import com.intellispace.backend.catalog.domain.CatalogItem;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CatalogItemQueryService implements CatalogItemQueryUseCase {

    private final CatalogItemJpaRepository jpaRepository;

    public CatalogItemQueryService(CatalogItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsById(UUID catalogItemId) {
        return jpaRepository.existsById(catalogItemId);
    }

    @Override
    public List<CatalogItem> listCatalogItems() {
        return jpaRepository.findAll(
                        Sort.by(Sort.Order.asc("category"), Sort.Order.asc("name")))
                .stream()
                .map(entity -> new CatalogItem(
                        entity.getId(),
                        entity.getSlug(),
                        entity.getName(),
                        entity.getCategory(),
                        entity.getDescription(),
                        entity.getModelPath(),
                        entity.getBaseWidth(),
                        entity.getBaseDepth(),
                        entity.getBaseHeight(),
                        entity.getDefaultScaleX(),
                        entity.getDefaultScaleY(),
                        entity.getDefaultScaleZ(),
                        entity.getDefaultRotX(),
                        entity.getDefaultRotY(),
                        entity.getDefaultRotZ(),
                        entity.getSnapHeight(),
                        entity.getPrice(),
                        entity.getCurrency(),
                        entity.getStyle(),
                        entity.getMaterial(),
                        entity.getColor(),
                        entity.getRoomType()))
                .toList();
    }
}
