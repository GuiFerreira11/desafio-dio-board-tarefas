package desafio.dio.board.tarefa.persistence.dto;

import desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
    String name,
    BoardColumnKindEnum kind,
    int cardsAmount) {
}
