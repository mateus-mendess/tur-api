CREATE TABLE IF NOT EXISTS favorites(
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users(id),
    tourist_point_id uuid NOT NULL REFERENCES tourist_points(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT uk_favorites_user_tourist_point UNIQUE (user_id, tourist_point_id)
);