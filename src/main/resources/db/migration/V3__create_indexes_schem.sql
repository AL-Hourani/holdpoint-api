

CREATE INDEX idx_reservations_resource_id
    ON reservations(resource_id);

CREATE INDEX idx_reservations_expiration
    ON reservations(expires_at)
    WHERE status = 'HELD';

