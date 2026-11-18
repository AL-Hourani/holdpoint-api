
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    resource_id UUID NOT NULL,

    quantity BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL,

    reference VARCHAR(150),

    expires_at TIMESTAMPTZ NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservation_resource
        FOREIGN KEY (resource_id)
        REFERENCES resources(id),

    CONSTRAINT chk_reservation_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_reservation_status
        CHECK (
            status IN (
                'HELD',
                'CONFIRMED',
                'CANCELLED',
                'EXPIRED',
                'RELEASED'
            )
        )
);

