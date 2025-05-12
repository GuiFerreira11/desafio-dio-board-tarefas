package desafio.dio.board.tarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.dao.CardDao;
import desafio.dio.board.tarefa.persistence.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardQuerryService {

  private final Connection connection;

  public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
    CardDao dao = new CardDao(connection);
    return dao.findById(id);
  }

}
