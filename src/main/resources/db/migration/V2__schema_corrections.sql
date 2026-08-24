-- ============================================================
-- V2 — Schema corrections and production-grade improvements
-- ============================================================


-- ============================================================
-- Fix 1 (CRITICAL): Rename material_override -> material_overrides
-- The JPA entity maps this column as "material_overrides" (plural).
-- Without this rename, Hibernate validate mode throws SchemaValidationException at startup.
-- ============================================================
ALTER TABLE workspace_furniture
    RENAME COLUMN material_override TO material_overrides;


-- ============================================================
-- Fix 2: Align catalog_item.name length with JPA annotation (150)
-- @Column(length = 150) in CatalogItemEntity; DB had VARCHAR(255).
-- ============================================================
ALTER TABLE catalog_item
    ALTER COLUMN name TYPE VARCHAR(150);


-- ============================================================
-- Fix 3: Align workspace.name length with API validation (@Size(max = 200))
-- CreateWorkspaceRequest validates max 200; DB had VARCHAR(255).
-- ============================================================
ALTER TABLE workspace
    ALTER COLUMN name TYPE VARCHAR(200);


-- ============================================================
-- Fix 4: Explicit ON DELETE RESTRICT on workspace_furniture.catalog_item_id
-- PostgreSQL defaults to RESTRICT but explicit declaration documents intent.
-- Prevents deletion of a catalog item that is referenced by any workspace furniture.
-- ============================================================
ALTER TABLE workspace_furniture
    DROP CONSTRAINT IF EXISTS workspace_furniture_catalog_item_id_fkey;

ALTER TABLE workspace_furniture
    ADD CONSTRAINT workspace_furniture_catalog_item_id_fkey
        FOREIGN KEY (catalog_item_id)
            REFERENCES catalog_item (id)
            ON DELETE RESTRICT;


-- ============================================================
-- Fix 5: Auto-maintain updated_at on every UPDATE via a trigger function.
-- Hibernate @UpdateTimestamp handles app-layer updates, but this ensures
-- updated_at stays accurate for any out-of-band DB writes (migrations, scripts).
-- ============================================================
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_app_user_updated_at
    BEFORE UPDATE ON app_user
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_catalog_item_updated_at
    BEFORE UPDATE ON catalog_item
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_workspace_updated_at
    BEFORE UPDATE ON workspace
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_workspace_furniture_updated_at
    BEFORE UPDATE ON workspace_furniture
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_workspace_architecture_updated_at
    BEFORE UPDATE ON workspace_architecture
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
