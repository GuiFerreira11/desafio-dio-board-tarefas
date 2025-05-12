package desafio.dio.board.tarefa.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConfig {

  private static Dotenv dotenv = Dotenv.load();

  private static String url = dotenv.get("DB_URL");
  private static String user = dotenv.get("DB_USER");
  private static String password = dotenv.get("DB_PASSWORD");

  public static Connection getConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(url, user, password);
    connection.setAutoCommit(false);
    return connection;
  }

}
