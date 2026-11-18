CREATE TABLE  resources (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    resource_key VARCHAR(150) NOT NULL UNIQUE,

    capacity BIGINT NOT NULL,

    reserved_quantity BIGINT NOT NULL DEFAULT 0,

    confirmed_quantity BIGINT NOT NULL DEFAULT 0,


    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_resource_capacity
        CHECK(capacity > 0),

    CONSTRAINT chk_reserved_quantity
        CHECK(reserved_quantity >= 0),

    CONSTRAINT chk_confirmed_quantity
        CHECK (confirmed_quantity >= 0),


    CONSTRAINT chk_resource_availability
        CHECK (
            reserved_quantity + confirmed_quantity <= capacity
        )

);



