ALTER TABLE addresses DROP COLUMN tourist_point_id;

ALTER TABLE tourist_points
    ADD COLUMN address_id UUID NOT NULL,
    ADD CONSTRAINT fk_tourist_points_address
        FOREIGN KEY (address_id) REFERENCES addresses(id);