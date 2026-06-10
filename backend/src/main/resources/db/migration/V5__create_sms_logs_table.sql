-- =============================================================================
-- V5__create_sms_logs_table.sql
-- Creates the SMS_LOGS table as defined in the ER diagram (Section 4.1).
-- This table is used by Member 2 (SMS & Admin Reporting Module).
-- Defined here so the full schema is version-controlled in one place.
-- =============================================================================

CREATE TABLE IF NOT EXISTS sms_logs (
    id           BIGSERIAL    PRIMARY KEY,
    payment_id   BIGINT       REFERENCES payments (id),
    officer_phone VARCHAR(20) NOT NULL,
    message      TEXT         NOT NULL,
    status       VARCHAR(10)  NOT NULL DEFAULT 'PENDING'
                              CHECK (status IN ('SENT', 'FAILED', 'PENDING')),
    sent_at      TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index for querying SMS logs by payment
CREATE INDEX IF NOT EXISTS idx_sms_logs_payment_id ON sms_logs (payment_id);
-- Index for monitoring failed SMS deliveries
CREATE INDEX IF NOT EXISTS idx_sms_logs_status     ON sms_logs (status);

COMMENT ON TABLE  sms_logs              IS 'Audit log of SMS notifications sent to police officers after payment';
COMMENT ON COLUMN sms_logs.payment_id   IS 'FK to the payment that triggered this SMS';
COMMENT ON COLUMN sms_logs.status       IS 'SENT | FAILED | PENDING';
COMMENT ON COLUMN sms_logs.sent_at      IS 'NULL if still PENDING or FAILED; set when delivery confirmed';
