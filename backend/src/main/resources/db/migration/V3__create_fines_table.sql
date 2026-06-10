-- =============================================================================
-- V3__create_fines_table.sql
-- Creates the FINES table as defined in the ER diagram (Section 4.1).
-- Key design decision: reference_number = unique alphanumeric code on the fine sheet
--   e.g., TF-2026-WP-00001 (Section 4.2 Key Design Decisions).
-- =============================================================================

CREATE TABLE IF NOT EXISTS fines (
    id               BIGSERIAL    PRIMARY KEY,
    reference_number VARCHAR(30)  NOT NULL UNIQUE,
    officer_id       BIGINT       NOT NULL REFERENCES users (id),
    category_id      BIGINT       NOT NULL REFERENCES fine_categories (id),
    driver_license_no VARCHAR(20) NOT NULL,
    driver_name      VARCHAR(100) NOT NULL,
    vehicle_number   VARCHAR(15)  NOT NULL,
    district         VARCHAR(50)  NOT NULL,
    location         VARCHAR(255),
    status           VARCHAR(10)  NOT NULL DEFAULT 'PENDING'
                                  CHECK (status IN ('PENDING', 'PAID', 'EXPIRED')),
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index on reference_number: primary lookup path for drivers (NFR: < 500ms)
CREATE INDEX IF NOT EXISTS idx_fines_reference_number ON fines (reference_number);
-- Index on category_id: used in admin reporting queries (district/category breakdown)
CREATE INDEX IF NOT EXISTS idx_fines_category_id     ON fines (category_id);
-- Index on officer_id: used in GET /api/fines/officer/{officerId}
CREATE INDEX IF NOT EXISTS idx_fines_officer_id      ON fines (officer_id);
-- Index on status: used in filtering PENDING fines
CREATE INDEX IF NOT EXISTS idx_fines_status          ON fines (status);

COMMENT ON TABLE  fines                  IS 'Traffic fines issued by police officers';
COMMENT ON COLUMN fines.reference_number IS 'Unique code printed on physical fine sheet, e.g. TF-2026-WP-00001';
COMMENT ON COLUMN fines.status           IS 'PENDING | PAID | EXPIRED';
