CREATE TABLE blocks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    block_cause VARCHAR(255) NOT NULL,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unblock_cause VARCHAR(255),
    unblocked_at TIMESTAMP,
    card_id BIGINT NOT NULL,
    CONSTRAINT fk_cards__blocks FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
) ENGINE=InnoDB;
