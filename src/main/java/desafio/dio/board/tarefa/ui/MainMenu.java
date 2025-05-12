package desafio.dio.board.tarefa.ui;

import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.FINAL;
import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.INITIAL;
import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.PENDING;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import desafio.dio.board.tarefa.persistence.config.ConnectionConfig;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum;
import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import desafio.dio.board.tarefa.service.BoardQueryService;
import desafio.dio.board.tarefa.service.BoardService;

public class MainMenu {

  private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

  public void execute() throws SQLException {

    System.out.println("========================================");
    System.out.println("== Bem vindo ao gerenciado de Tarefas ==");
    System.out.println("========================================");

    System.out.println();

    int option = -1;

    while (true) {
      System.out.println("Escolha a opção desejada:\n");
      System.out.println("1 - Criar um novo board de tarefas");
      System.out.println("2 - Selecionar um board existente");
      System.out.println("3 - Excluir um board");
      System.out.println("0 - Sair");
      option = scanner.nextInt();
      switch (option) {
        case 1 -> createBoard();
        case 2 -> selectBoard();
        case 3 -> deleteBoard();
        case 0 -> System.exit(0);
        default -> System.out.println("Opção inválida, informe uma das opções do menu");
      }
    }

  }

  private void createBoard() throws SQLException {
    BoardEntity entity = new BoardEntity();
    System.out.println("Informe o nome do board que deseja criar");
    entity.setName(scanner.next());

    System.out
        .println("Seu board terá colunas além das 3 colunas padrões? Se sim, informe a quantidade, se não, digite `0`");
    int additionalColumns = scanner.nextInt();

    List<BoardColumnEntity> columns = new ArrayList<>();

    System.out.println("Informe o nome da coluna inicial do board");
    String initialColumnName = scanner.next();
    BoardColumnEntity initialColumn = createColumn(initialColumnName, INITIAL, 0);
    columns.add(initialColumn);

    for (int i = 0; i < additionalColumns; i++) {
      System.out.println("Informe o nome da coluna de tarefa pendente");
      String pendingColumnName = scanner.next();
      BoardColumnEntity pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
      columns.add(pendingColumn);
    }

    System.out.println("Informe o nome da coluna final");
    String finalColumnName = scanner.next();
    BoardColumnEntity finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
    columns.add(finalColumn);

    System.out.println("Informe o nome da coluna de cancelamento do board");
    String cancelColumnName = scanner.next();
    BoardColumnEntity cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
    columns.add(cancelColumn);

    entity.setBoardColumns(columns);

    try (Connection connection = ConnectionConfig.getConnection()) {
      BoardService service = new BoardService(connection);
      service.create(entity);
    }
  }

  private void selectBoard() throws SQLException {
    System.out.println("Informe o id do board que deseja selecionar");
    Long id = scanner.nextLong();
    try (Connection connection = ConnectionConfig.getConnection()) {
      BoardQueryService queryService = new BoardQueryService(connection);
      Optional<BoardEntity> optional = queryService.findById(id);
      optional.ifPresentOrElse(b -> new BoardMenu(b).execute(),
          () -> System.out.println("Não foi encontrado nenhum boar com o id %s".formatted(id)));
    }
  }

  private void deleteBoard() throws SQLException {
    System.out.println("Informe o id do board que deseja excluir");
    Long id = scanner.nextLong();
    try (Connection connection = ConnectionConfig.getConnection()) {
      BoardService service = new BoardService(connection);
      if (service.delete(id)) {
        System.out.println("O board %s foi excluido com sucesso".formatted(id));
      } else {
        System.out.println("Não foi encontrado o board com o id %s".formatted(id));
      }
    }
  }

  private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
    BoardColumnEntity boardColumn = new BoardColumnEntity();
    boardColumn.setName(name);
    boardColumn.setKind(kind);
    boardColumn.setOrder(order);
    return boardColumn;
  }

}
