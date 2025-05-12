package desafio.dio.board.tarefa.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import desafio.dio.board.tarefa.persistence.converter.OffsetDateTimeConverter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BlockDAO {

  private Connection connection;

  public void block(final Long cardId, final String cause) throws SQLException {
    String sql = "INSERT INTO blocks (blocked_at, block_cause, card_id) VALUES (?, ?, ?);";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setTimestamp(1, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()));
      statement.setString(2, cause);
      statement.setLong(3, cardId);
      statement.executeUpdate();
    }
  }

  public void unblock(final Long cardId, final String cause) throws SQLException {
    String sql = "UPDATE blocks SET unblocked_at = ?, unblock_cause = ? WHERE card_id = ? AND unblock_cause IS NULL;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setTimestamp(1, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()));
      statement.setString(2, cause);
      statement.setLong(3, cardId);
      statement.executeUpdate();
    }
  }

}
