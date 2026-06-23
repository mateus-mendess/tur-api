DROP TABLE addresses;
DROP TABLE states;

CREATE TABLE states(
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL,
    abbreviation CHAR(2) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE addresses(
    id uuid PRIMARY KEY,
    street VARCHAR(60),
    number VARCHAR(6),
    complement VARCHAR(40),
    neighborhood VARCHAR(60),
    city VARCHAR(60) NOT NULL,
    zip_code VARCHAR(9) NOT NULL,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(9, 6) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    tourist_point_id uuid NOT NULL,
    state_id int NOT NULL,
    FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id),
    FOREIGN KEY (state_id) REFERENCES states(id)
);