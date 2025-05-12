package desafio.dio.board.tarefa.persistence.dto;

import desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}
