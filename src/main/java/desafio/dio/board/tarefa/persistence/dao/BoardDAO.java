package desafio.dio.board.tarefa.persistence.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardDAO {

  private Connection connection;

  public BoardEntity create(final BoardEntity entity) throws SQLException {
    String sql = "INSERT INTO boards (name) VALUES (?);";
    try (PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
      statement.setString(1, entity.getName());
      statement.executeUpdate();
      ResultSet resultSet = statement.getGeneratedKeys();
      if (resultSet.next()) {
        entity.setId(resultSet.getLong(1));
      }
      return entity;
    }
  }

  public Optional<BoardEntity> findById(final Long id) throws SQLException {
    String sql = "SELECT id, name FROM boards WHERE id = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      statement.executeQuery();
      ResultSet resultSet = statement.getResultSet();
      if (resultSet.next()) {
        BoardEntity entity = new BoardEntity();
        entity.setId(resultSet.getLong("id"));
        entity.setName(resultSet.getString("name"));
        return Optional.of(entity);
      }
      return Optional.empty();
    }
  }

  public void delete(final Long id) throws SQLException {
    String sql = "DELETE FROM boards WHERE id = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      statement.executeUpdate();
    }
  }

  public boolean exists(final Long id) throws SQLException {
    String sql = "SELECT * FROM boards WHERE id = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      statement.executeQuery();
      return statement.getResultSet().next();
    }
  }

}
