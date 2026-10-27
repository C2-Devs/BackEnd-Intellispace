ALTER TABLE catalog_item
    ADD COLUMN workspace_id UUID REFERENCES workspace(id) ON DELETE CASCADE;

ALTER TABLE catalog_item
    ADD COLUMN thumbnail_path TEXT;

CREATE INDEX idx_catalog_item_workspace_id
    ON catalog_item(workspace_id);
