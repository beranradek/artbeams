-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_system_event_log_table.sql
-- System event log for tracking important failures (and optionally warnings) across key flows.
CREATE TABLE system_event_log (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    event_time timestamp NOT NULL,
    severity VARCHAR(16) NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    origin VARCHAR(60) DEFAULT NULL,
    message VARCHAR(2000) NOT NULL,
    details TEXT DEFAULT NULL,
    stack_trace TEXT DEFAULT NULL,
    entity_type VARCHAR(20) DEFAULT NULL,
    entity_id VARCHAR(40) DEFAULT NULL,
    user_id VARCHAR(40) DEFAULT NULL,
    ip_address VARCHAR(60) DEFAULT NULL,
    user_agent VARCHAR(200) DEFAULT NULL,
    correlation_id VARCHAR(64) DEFAULT NULL
);

CREATE INDEX idx_system_event_log_time ON system_event_log (event_time DESC);
CREATE INDEX idx_system_event_log_type_time ON system_event_log (event_type, event_time DESC);
CREATE INDEX idx_system_event_log_severity_time ON system_event_log (severity, event_time DESC);
CREATE INDEX idx_system_event_log_entity ON system_event_log (entity_type, entity_id, event_time DESC);
CREATE INDEX idx_system_event_log_user_time ON system_event_log (user_id, event_time DESC);
