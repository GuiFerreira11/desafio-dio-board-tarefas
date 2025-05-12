package desafio.dio.board.tarefa.service;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import desafio.dio.board.tarefa.persistence.dao.BoardColumnDAO;
import desafio.dio.board.tarefa.persistence.dao.BoardDAO;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardService {

  private final Connection connection;

  public BoardEntity create(final BoardEntity entity) throws SQLException {
    BoardDAO dao = new BoardDAO(connection);
    BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
    try {
      dao.create(entity);
      List<BoardColumnEntity> columns = entity.getBoardColumns().stream()
          .map(c -> {
            c.setBoard(entity);
            return c;
          }).toList();
      for (BoardColumnEntity column : columns) {
        boardColumnDAO.create(column);
      }
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
    return entity;
  }

  public boolean delete(final Long id) throws SQLException {
    BoardDAO dao = new BoardDAO(connection);
    try {
      if (!dao.exists(id)) {
        return false;
      }
      dao.delete(id);
      connection.commit();
      return true;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }
}
