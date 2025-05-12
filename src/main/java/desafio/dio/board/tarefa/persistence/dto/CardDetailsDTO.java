package desafio.dio.board.tarefa.persistence.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
    boolean blocked,
    OffsetDateTime bolckedAt,
    String blockCause,
    int blockAmount,
    Long columnId,
    String columnName) {
}
