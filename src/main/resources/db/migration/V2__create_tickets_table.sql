CREATE TABLE tickets (
                         id BIGSERIAL PRIMARY KEY,

                         title VARCHAR(255) NOT NULL,

                         description VARCHAR(2000) NOT NULL,

                         status VARCHAR(255) NOT NULL,

                         priority VARCHAR(255) NOT NULL,

                         created_by_id BIGINT NOT NULL,

                         created_at TIMESTAMP NOT NULL,

                         updated_at TIMESTAMP NOT NULL,

                         CONSTRAINT fk_tickets_created_by
                             FOREIGN KEY (created_by_id)
                                 REFERENCES users(id)
);