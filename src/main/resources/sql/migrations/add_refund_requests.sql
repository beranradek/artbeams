-- Customer refund requests. Financial reimbursement is completed manually by an administrator.
CREATE TABLE IF NOT EXISTS refund_requests (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
    order_id VARCHAR(40) NOT NULL,
    user_id VARCHAR(40) NOT NULL,
    reason VARCHAR(2000) DEFAULT NULL,
    status VARCHAR(16) NOT NULL,
    requested_at timestamp NOT NULL,
    resolved_at timestamp DEFAULT NULL
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'refund_request_order_fk') THEN
        ALTER TABLE refund_requests ADD CONSTRAINT refund_request_order_fk
            FOREIGN KEY (order_id) REFERENCES orders (id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'refund_request_user_fk') THEN
        ALTER TABLE refund_requests ADD CONSTRAINT refund_request_user_fk
            FOREIGN KEY (user_id) REFERENCES users (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_refund_requests_status_requested ON refund_requests (status, requested_at DESC);
CREATE INDEX IF NOT EXISTS idx_refund_requests_user_requested ON refund_requests (user_id, requested_at DESC);
CREATE UNIQUE INDEX IF NOT EXISTS idx_refund_requests_open_order ON refund_requests (order_id) WHERE status = 'REQUESTED';
