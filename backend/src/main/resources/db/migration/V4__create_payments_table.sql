-- =============================================================================
-- V4__create_payments_table.sql
-- Creates the PAYMENTS table as defined in the ER diagram (Section 4.1).
-- One-to-one relationship with FINES (UNIQUE constraint on fine_id).
-- =============================================================================

CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL      PRIMARY KEY,
    fine_id         BIGINT         NOT NULL UNIQUE REFERENCES fines (id),
    amount_paid     DECIMAL(10, 2) NOT NULL CHECK (amount_paid > 0),
    payment_method  VARCHAR(20)    NOT NULL CHECK (payment_method IN ('CARD', 'MOBILE_WALLET')),
    transaction_ref VARCHAR(100)   NOT NULL,
    payment_channel VARCHAR(20)    NOT NULL CHECK (payment_channel IN ('MOBILE_APP', 'WEB_PORTAL')),
    paid_at         TIMESTAMP      NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Index for fast receipt lookup by fine_id
CREATE INDEX IF NOT EXISTS idx_payments_fine_id ON payments (fine_id);

COMMENT ON TABLE  payments                  IS 'Payments made by drivers to settle traffic fines';
COMMENT ON COLUMN payments.fine_id          IS 'One-to-one FK to fines; UNIQUE prevents double payment';
COMMENT ON COLUMN payments.payment_method   IS 'CARD | MOBILE_WALLET';
COMMENT ON COLUMN payments.payment_channel  IS 'MOBILE_APP | WEB_PORTAL';
COMMENT ON COLUMN payments.transaction_ref  IS 'External gateway transaction reference for audit trail';
COMMENT ON COLUMN payments.paid_at          IS 'Exact timestamp the payment was processed';
