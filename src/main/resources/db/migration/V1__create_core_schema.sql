-- ============================================================
-- Enum types (must exist before any table references them)
-- ============================================================
CREATE TYPE architectural_type AS ENUM('window', 'door');
CREATE TYPE architectural_wall AS ENUM('left', 'right', 'front', 'back');



-- ============================================================
-- app_user — minimal identity record; full auth fields land in Step 5
-- ============================================================
CREATE TABLE app_user
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email            VARCHAR(255) UNIQUE NOT NULL,
    password_hash    TEXT NOT NULL,
    display_name     VARCHAR(255),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);



-- ============================================================
-- catalog_item — shared master furniture data (its own module)
-- ============================================================
CREATE TABLE catalog_item
(
    id              UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    slug            VARCHAR(100) UNIQUE NOT NULL,
    name            VARCHAR(255)        NOT NULL,
    category        VARCHAR(100)        NOT NULL,
    description     TEXT,

    model_path      TEXT,

    -- Physical dimensions at scale 1.0. Nullable per spec — see deviation note #2.
    base_width      DOUBLE PRECISION,
    base_depth      DOUBLE PRECISION,
    base_height     DOUBLE PRECISION,

    default_scale_x DOUBLE PRECISION    NOT NULL DEFAULT 1.0,
    default_scale_y DOUBLE PRECISION    NOT NULL DEFAULT 1.0,
    default_scale_z DOUBLE PRECISION    NOT NULL DEFAULT 1.0,

    default_rot_x   DOUBLE PRECISION    NOT NULL DEFAULT 0.0,
    default_rot_y   DOUBLE PRECISION    NOT NULL DEFAULT 0.0,
    default_rot_z   DOUBLE PRECISION    NOT NULL DEFAULT 0.0,

    snap_height     DOUBLE PRECISION    NOT NULL DEFAULT 0.0,

    price           NUMERIC(12,2),
    currency        VARCHAR(3) DEFAULT 'INR',

    style           VARCHAR(50),
    material        VARCHAR(100),
    color           VARCHAR(50),
    room_type       VARCHAR(50),

    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_base_dimensions_positive CHECK (
        (base_width  IS NULL OR base_width  > 0) AND
        (base_depth  IS NULL OR base_depth  > 0) AND
        (base_height IS NULL OR base_height > 0)
        ),
    CONSTRAINT check_price_non_negative CHECK (price IS NULL OR price >= 0)
);


-- ============================================================
-- workspace — a user's room/design
-- ============================================================
CREATE TABLE workspace
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id          UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,

    name             VARCHAR(255) NOT NULL,
    description      TEXT,

    room_type        VARCHAR(50),
    design_style     VARCHAR(50),

    room_width       DOUBLE PRECISION NOT NULL,
    room_depth       DOUBLE PRECISION NOT NULL,
    room_height      DOUBLE PRECISION NOT NULL,
    wall_thickness   DOUBLE PRECISION NOT NULL DEFAULT 0.15,

    wall_color       VARCHAR(7) NOT NULL,
    floor_color      VARCHAR(7) NOT NULL,
    ceiling_color    VARCHAR(7) NOT NULL,
    light_preset     VARCHAR(50) NOT NULL DEFAULT 'day',

    budget           NUMERIC(12,2),
    currency         VARCHAR(3) DEFAULT 'INR',

    version          INTEGER NOT NULL DEFAULT 0,

    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_room_dimensions_positive CHECK (
        room_width > 0 AND room_depth > 0 AND room_height > 0 AND wall_thickness > 0
        ),
    CONSTRAINT check_budget_non_negative CHECK (budget IS NULL OR budget >= 0)
);


-- ============================================================
-- workspace_furniture — one placed instance of a catalog item
-- ============================================================
CREATE TABLE workspace_furniture
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    workspace_id       UUID NOT NULL REFERENCES workspace(id) ON DELETE CASCADE,
    catalog_item_id    UUID NOT NULL REFERENCES catalog_item(id),

    pos_x              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    pos_y              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    pos_z              DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    rot_x              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    rot_y              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    rot_z              DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    scale_x            DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    scale_y            DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    scale_z            DOUBLE PRECISION NOT NULL DEFAULT 1.0,

    is_locked          BOOLEAN NOT NULL DEFAULT FALSE,
    is_visible         BOOLEAN NOT NULL DEFAULT TRUE,

    material_override  JSONB,

    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_scale_positive CHECK (
        scale_x > 0 AND scale_y > 0 AND scale_z > 0
        )
);




-- ============================================================
-- workspace_architecture — doors and windows belonging to a workspace
-- ============================================================
CREATE TABLE workspace_architecture
(
     id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),

     workspace_id   UUID NOT NULL REFERENCES workspace(id) ON DELETE CASCADE,

     element_type   architectural_type NOT NULL,
     wall           architectural_wall NOT NULL,

     wall_position  DOUBLE PRECISION NOT NULL,
     width          DOUBLE PRECISION NOT NULL,
     height         DOUBLE PRECISION NOT NULL,
     sill_height    DOUBLE PRECISION NOT NULL DEFAULT 0.0,

     created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT check_dimensions_positive CHECK (width > 0 AND height > 0),
     CONSTRAINT check_wall_position_non_negative CHECK (wall_position >= 0),
     CONSTRAINT check_sill_height_non_negative CHECK (sill_height >= 0)

);



-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_workspace_user_id ON workspace (user_id);
CREATE INDEX idx_workspace_furniture_workspace_id ON workspace_furniture (workspace_id);
CREATE INDEX idx_workspace_furniture_catalog_item_id ON workspace_furniture (catalog_item_id);
CREATE INDEX idx_workspace_architecture_workspace_id ON workspace_architecture (workspace_id);