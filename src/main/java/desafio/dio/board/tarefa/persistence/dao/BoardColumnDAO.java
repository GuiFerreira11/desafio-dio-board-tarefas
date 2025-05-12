package desafio.dio.board.tarefa.persistence.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.dto.BoardColumnDTO;
import desafio.dio.board.tarefa.persistence.entity.CardEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum;
import lombok.AllArgsConstructor;

import static java.util.Objects.isNull;

@AllArgsConstructor
public class BoardColumnDAO {

  private Connection connection;

  public BoardColumnEntity create(final BoardColumnEntity entity) throws SQLException {
    String sql = "INSERT INTO boards_columns (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
      statement.setString(1, entity.getName());
      statement.setInt(2, entity.getOrder());
      statement.setString(3, entity.getKind().name());
      statement.setLong(4, entity.getBoard().getId());
      statement.executeUpdate();
      ResultSet resultSet = statement.getGeneratedKeys();
      if (resultSet.next()) {
        entity.setId(resultSet.getLong(1));
      }
      return entity;
    }
  }

  public List<BoardColumnEntity> findByBoardId(Long boardId) throws SQLException {
    String sql = "SELECT id, name, `order`, kind FROM boards_columns WHERE board_id = ? ORDER BY `order`;";
    List<BoardColumnEntity> entities = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, boardId);
      statement.executeQuery();
      ResultSet resultSet = statement.getResultSet();
      while (resultSet.next()) {
        BoardColumnEntity entity = new BoardColumnEntity();
        entity.setId(resultSet.getLong("id"));
        entity.setName(resultSet.getString("name"));
        entity.setOrder(resultSet.getInt("order"));
        entity.setKind(BoardColumnKindEnum.findByName(resultSet.getString("kind")));
        entities.add(entity);
      }
    }
    return entities;
  }

  public List<BoardColumnDTO> findByBoardIdWithDetails(Long boardId) throws SQLException {
    String sql = """
        SELECT bc.id,
               bc.name,
               bc.kind,
               (SELECT COUNT(c.id)
                     FROM cards c
                     WHERE c.boards_column_id = bc.id) cards_amount
        FROM boards_columns bc
        WHERE board_id = ?
        ORDER BY `order`;
        """;
    List<BoardColumnDTO> dtos = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, boardId);
      statement.executeQuery();
      ResultSet resultSet = statement.getResultSet();
      while (resultSet.next()) {
        BoardColumnDTO dto = new BoardColumnDTO(
            resultSet.getLong("bc.id"),
            resultSet.getString("bc.name"),
            BoardColumnKindEnum.findByName(resultSet.getString("bc.kind")),
            resultSet.getInt("cards_amount"));
        dtos.add(dto);
      }
    }
    return dtos;
  }

  public Optional<BoardColumnEntity> findById(Long boardId) throws SQLException {
    String sql = """
        SELECT bc.name,
               bc.kind,
               c.id,
               c.title,
               c.description
          FROM boards_columns bc
          LEFT JOIN cards c
            ON c.board_column_id = bc.id
         WHERE bc.id = ?;
        """;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, boardId);
      statement.executeQuery();
      ResultSet resultSet = statement.getResultSet();
      if (resultSet.next()) {
        BoardColumnEntity entity = new BoardColumnEntity();
        entity.setName(resultSet.getString("bc.name"));
        entity.setKind(BoardColumnKindEnum.findByName(resultSet.getString("bc.kind")));
        do {
          if (isNull(resultSet.getString("c.title"))) {
            break;
          }
          CardEntity card = new CardEntity();
          card.setId(resultSet.getLong("c.id"));
          card.setTitle(resultSet.getString("c.title"));
          card.setDescription(resultSet.getString("c.description"));
          entity.getCards().add(card);
        } while (resultSet.next());
        return Optional.of(entity);
      }
    }
    return Optional.empty();
  }
}
