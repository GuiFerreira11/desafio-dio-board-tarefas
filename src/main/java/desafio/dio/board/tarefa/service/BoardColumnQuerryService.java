package desafio.dio.board.tarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.dao.BoardColumnDAO;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardColumnQuerryService {

  private final Connection connection;

  public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
    BoardColumnDAO dao = new BoardColumnDAO(connection);
    return dao.findById(id);
  }
}
