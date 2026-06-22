CREATE TABLE users(
    id uuid PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE categories(
    id uuid PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,

    user_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tourist_points(
    id uuid PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    accessibility_info TEXT,
    has_accessibility BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    user_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tourist_point_categories(
    tourist_point_id uuid,
    category_id uuid,

    PRIMARY KEY (tourist_point_id, category_id),

    FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE photos(
                       id uuid PRIMARY KEY,
                       path VARCHAR(255) NOT NULL,
                       is_cover BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL,

                       tourist_point_id uuid NOT NULL,
                       FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id)
);

CREATE TABLE states(
                       id uuid PRIMARY KEY,
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
                          state_id uuid NOT NULL,
                          FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id),
                          FOREIGN KEY (state_id) REFERENCES states(id)
);

CREATE TABLE comments(
                         id uuid PRIMARY KEY,
                         content TEXT NOT NULL,
                         note SMALLINT NOT NULL CHECK (note BETWEEN 1 AND 5),
                         author_name VARCHAR(100),
                         created_at TIMESTAMP NOT NULL,

                         tourist_point_id uuid NOT NULL,

                         FOREIGN KEY (tourist_point_id) REFERENCES tourist_points(id)
);