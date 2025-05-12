package desafio.dio.board.tarefa.persistence.migration;

import org.flywaydb.core.Flyway;

import io.github.cdimascio.dotenv.Dotenv;

public class FlywayConfig {

  private static Dotenv dotenv = Dotenv.load();

  private static String url = dotenv.get("DB_URL");
  private static String user = dotenv.get("DB_USER");
  private static String password = dotenv.get("DB_PASSWORD");

  private static Flyway flyway;

  public static void flywayConfig() {
    flyway = Flyway.configure().dataSource(url, user, password).load();
    flyway.migrate();
  }

}
