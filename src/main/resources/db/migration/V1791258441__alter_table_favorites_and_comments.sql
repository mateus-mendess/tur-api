ALTER TABLE favorites
DROP CONSTRAINT favorites_tourist_point_id_fkey,
    ADD CONSTRAINT fk_tourist_point
        FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id) ON DELETE CASCADE;

ALTER TABLE comments
DROP CONSTRAINT comments_tourist_point_id_fkey,
    ADD CONSTRAINT fk_comments_tourist_point
        FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id) ON DELETE CASCADE;