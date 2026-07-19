CREATE TABLE IF NOT EXISTS accessibility_types(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tourist_point_accessibility_types(
    tourist_point_id uuid NOT NULL,
    accessibility_id INT NOT NULL,

    PRIMARY KEY (tourist_point_id, accessibility_id),

    FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id),
    FOREIGN KEY (accessibility_id) REFERENCES accessibility_types(id)
);