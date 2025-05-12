package desafio.dio.board.tarefa;

import java.sql.SQLException;

import desafio.dio.board.tarefa.persistence.migration.FlywayConfig;
import desafio.dio.board.tarefa.ui.MainMenu;

public class Main {

  public static void main(String[] args) throws SQLException {

    FlywayConfig.flywayConfig();

    new MainMenu().execute();
  }
}
