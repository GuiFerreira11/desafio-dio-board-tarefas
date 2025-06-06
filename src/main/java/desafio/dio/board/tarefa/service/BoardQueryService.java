package desafio.dio.board.tarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.dto.BoardDetailsDTO;
import desafio.dio.board.tarefa.persistence.dto.BoardColumnDTO;
import desafio.dio.board.tarefa.persistence.dao.BoardColumnDAO;
import desafio.dio.board.tarefa.persistence.dao.BoardDAO;
import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardQueryService {

  private Connection connection;

  public Optional<BoardEntity> findById(Long id) throws SQLException {
    BoardDAO dao = new BoardDAO(connection);
    BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
    Optional<BoardEntity> optional = dao.findById(id);
    if (optional.isPresent()) {
      BoardEntity entity = optional.get();
      entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
      return Optional.of(entity);
    }
    return Optional.empty();
  }

  public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
    BoardDAO dao = new BoardDAO(connection);
    BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
    Optional<BoardEntity> optional = dao.findById(id);
    if (optional.isPresent()) {
      BoardEntity entity = optional.get();
      List<BoardColumnDTO> columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
      BoardDetailsDTO dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
      return Optional.of(dto);
    }
    return Optional.empty();
  }

}
