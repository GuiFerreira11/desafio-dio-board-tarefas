package desafio.dio.board.tarefa.persistence.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import desafio.dio.board.tarefa.persistence.converter.OffsetDateTimeConverter;
import desafio.dio.board.tarefa.persistence.dto.CardDetailsDTO;
import desafio.dio.board.tarefa.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardDao {

  private final Connection connection;

  public CardEntity create(final CardEntity entity) throws SQLException {
    String sql = "INSERT INTO cards (title, description, boards_column_id) VALUES (?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
      statement.setString(1, entity.getTitle());
      statement.setString(2, entity.getDescription());
      statement.setLong(3, entity.getBoardColumn().getId());
      statement.executeUpdate();
      ResultSet resultSet = statement.getGeneratedKeys();
      if (resultSet.next()) {
        entity.setId(resultSet.getLong(1));
      }
      return entity;
    }
  }

  public void moveToColumn(final Long cardId, final Long columnId) throws SQLException {
    String sql = "UPDATE cards SET boards_column_id = ? WHERE id = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, columnId);
      statement.setLong(2, cardId);
      statement.executeUpdate();
    }
  }

  public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
    String sql = """
        SELECT c.id,
               c.title,
               c.description,
               b.blocked_at,
               b.block_cause,
               c.boards_column_id,
               bc.name,
               (SELECT COUNT(sub_b.id)
                       FROM blocks sub_b
                      WHERE sub_b.card_id = c.id) blocks_amount
          FROM cards c
          LEFT JOIN blocks b
            ON c.id = b.card_id
           AND b.unblocked_at IS NULL
         INNER JOIN boards_columns bc
            ON c.boards_column_id = bc.id
         WHERE id = ?;
        """;
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, id);
      statement.executeQuery();
      ResultSet resultSet = statement.getResultSet();
      if (resultSet.next()) {
        CardDetailsDTO dto = new CardDetailsDTO(
            resultSet.getLong("c.id"),
            resultSet.getString("c.title"),
            resultSet.getString("c.description"),
            nonNull(resultSet.getString("b.block_cause")),
            OffsetDateTimeConverter.toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
            resultSet.getString("b.block_cause"),
            resultSet.getInt("blocks_amount"),
            resultSet.getLong("c.boards_column_id"),
            resultSet.getString("bc.name"));

        return Optional.of(dto);
      }
    }
    return Optional.empty();
  }
}
