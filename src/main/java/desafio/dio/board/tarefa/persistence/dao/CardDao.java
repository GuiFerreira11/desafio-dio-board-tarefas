package desafio.dio.board.tarefa.persistence.dao;

import java.sql.Connection;

import desafio.dio.board.tarefa.persistence.dto.CardDetailsDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardDao {

  private final Connection connection;

  public CardDetailsDTO findById(final Long id) {

    return null;

  }
}
