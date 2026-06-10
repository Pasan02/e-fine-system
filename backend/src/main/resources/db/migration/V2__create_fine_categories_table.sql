-- =============================================================================
-- V2__create_fine_categories_table.sql
-- Creates the FINE_CATEGORIES table as defined in the ER diagram (Section 4.1).
-- Key design decision: fine amounts are FIXED per category (not variable per officer).
-- Soft-delete via is_active flag (Section 4.2 Key Design Decisions).
-- =============================================================================

CREATE TABLE IF NOT EXISTS fine_categories (
    id            BIGSERIAL      PRIMARY KEY,
    category_code VARCHAR(20)    NOT NULL UNIQUE,
    description   VARCHAR(255)   NOT NULL,
    amount        DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Index on category_code for fast lookup (NFR: API response < 500ms)
CREATE INDEX IF NOT EXISTS idx_fine_categories_code ON fine_categories (category_code);

COMMENT ON TABLE  fine_categories              IS 'Traffic violation categories with fixed fine amounts';
COMMENT ON COLUMN fine_categories.category_code IS 'e.g., SPD01 (speeding), SIG01 (signal violation)';
COMMENT ON COLUMN fine_categories.amount        IS 'Fixed fine amount in LKR for this category';
COMMENT ON COLUMN fine_categories.is_active     IS 'Soft-delete flag: false = category deactivated, not deleted';
