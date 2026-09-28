CREATE TABLE ticket_comments (
                                 id BIGSERIAL PRIMARY KEY,

                                 content VARCHAR(2000) NOT NULL,

                                 ticket_id BIGINT NOT NULL,

                                 author_id BIGINT NOT NULL,

                                 created_at TIMESTAMP NOT NULL,

                                 CONSTRAINT fk_comments_ticket
                                     FOREIGN KEY (ticket_id)
                                         REFERENCES tickets(id),

                                 CONSTRAINT fk_comments_author
                                     FOREIGN KEY (author_id)
                                         REFERENCES users(id)
);