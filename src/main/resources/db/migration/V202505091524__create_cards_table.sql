CREATE TABLE cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    boards_column_id BIGINT NOT NULL,
    CONSTRAINT fk_boards_column__cards FOREIGN KEY (boards_column_id) REFERENCES boards_columns(id) ON DELETE CASCADE
) ENGINE=InnoDB;
