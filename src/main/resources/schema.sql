CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512) NOT NULL,
    requestor_id INTEGER NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    FOREIGN KEY (requestor_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN NOT NULL,
    request_id INTEGER NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
        FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITH TIME ZONE NOT NULL,
  end_date TIMESTAMP WITH TIME ZONE NOT NULL,
  item_id INTEGER NOT NULL,
  booker_id INTEGER NOT NULL,
  status VARCHAR(50),
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT status CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
  FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
  FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(512),
    item_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pr_comment PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
)