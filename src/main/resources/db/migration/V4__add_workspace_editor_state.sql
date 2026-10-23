ALTER TABLE workspace
    ADD COLUMN editor_state JSONB NOT NULL DEFAULT '{}'::jsonb;
