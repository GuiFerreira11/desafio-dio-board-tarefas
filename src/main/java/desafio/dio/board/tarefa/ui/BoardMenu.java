package desafio.dio.board.tarefa.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import desafio.dio.board.tarefa.persistence.config.ConnectionConfig;
import desafio.dio.board.tarefa.persistence.dto.BoardDetailsDTO;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import desafio.dio.board.tarefa.service.BoardQueryService;
import desafio.dio.board.tarefa.service.BoardColumnQuerryService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardMenu {

  private final BoardEntity boardEntity;

  private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

  public void execute() {
    try {
      System.out.println("========================================");
      System.out.println("========= Bem vindo ao board %s ========".formatted(boardEntity.getId()));
      System.out.println("========================================");
      int option = -1;
      while (option != 9) {
        System.out.println("Escolha a opção desejada:\n");
        System.out.println("1 - Criar um card");
        System.out.println("2 - Mover um card");
        System.out.println("3 - Bloquear um card");
        System.out.println("4 - Desbloquear um card");
        System.out.println("5 - Cancelar um card");
        System.out.println("6 - Visualizar board");
        System.out.println("7 - Visualizar coluna com cards");
        System.out.println("8 - Visualizar um card");
        System.out.println("9 - Voltar para o menu anterior");
        System.out.println("0 - Sair");
        option = scanner.nextInt();
        switch (option) {
          case 1 -> createCard();
          case 2 -> moveCard();
          case 3 -> blockCard();
          case 4 -> unblockCard();
          case 5 -> cancelCard();
          case 6 -> showBoard();
          case 7 -> showColumn();
          case 8 -> showCard();
          case 9 -> System.out.println("Voltando para o menu anterior");
          case 0 -> System.exit(0);
          default -> System.out.println("Opção inválida, informe uma das opções do menu");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  private void createCard() {
  }

  private void moveCard() {
  }

  private void blockCard() {
  }

  private void unblockCard() {
  }

  private void cancelCard() {
  }

  private void showBoard() throws SQLException {
    try (Connection connection = ConnectionConfig.getConnection()) {
      Optional<BoardDetailsDTO> optional = new BoardQueryService(connection).showBoardDetails(boardEntity.getId());
      optional.ifPresent(b -> {
        System.out.println("Board %s - %s".formatted(b.id(), b.name()));
        b.columns().forEach(c -> {
          System.out.println("Coluna %s - tipo: %s tem %s cards".formatted(c.name(), c.kind(), c.cardsAmount()));
        });
      });

    }
  }

  private void showColumn() throws SQLException {
    List<Long> columnsIds = boardEntity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
    Long selectedColumn = -1L;
    while (!columnsIds.contains(selectedColumn)) {
      System.out.println("Escolha um das colunas do board %s".formatted(boardEntity.getName()));
      boardEntity.getBoardColumns()
          .forEach(c -> System.out.println("%s - %s [%s]".formatted(c.getId(), c.getName(), c.getKind())));
      selectedColumn = scanner.nextLong();
    }
    try (Connection connection = ConnectionConfig.getConnection()) {
      Optional<BoardColumnEntity> column = new BoardColumnQuerryService(connection).findById(selectedColumn);
      column.ifPresent(co -> {
        System.out.println("Coluna %s tipo %s".formatted(co.getName(), co.getKind()));
        co.getCards().forEach(ca -> {
          System.out.println("Card %s - %s.\nDescrição: %s".formatted(ca.getId(), ca.getTitle(), ca.getDescription()));
        });
      });
    }

  }

  private void showCard() {
  }
}
