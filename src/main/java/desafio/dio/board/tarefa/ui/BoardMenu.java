package desafio.dio.board.tarefa.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import desafio.dio.board.tarefa.persistence.config.ConnectionConfig;
import desafio.dio.board.tarefa.persistence.dto.BoardColumnInfoDTO;
import desafio.dio.board.tarefa.persistence.dto.BoardDetailsDTO;
import desafio.dio.board.tarefa.persistence.dto.CardDetailsDTO;
import desafio.dio.board.tarefa.persistence.entity.BoardColumnEntity;
import desafio.dio.board.tarefa.persistence.entity.BoardEntity;
import desafio.dio.board.tarefa.persistence.entity.CardEntity;
import desafio.dio.board.tarefa.service.BoardQueryService;
import desafio.dio.board.tarefa.service.CardQuerryService;
import desafio.dio.board.tarefa.service.CardService;
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

  private void createCard() throws SQLException {
    CardEntity card = new CardEntity();
    System.out.println("Informe o título do card");
    card.setTitle(scanner.next());
    System.out.println("Informe a descrição do card");
    card.setTitle(scanner.next());
    card.setBoardColumn(boardEntity.getInitialColumn());
    try (Connection connection = ConnectionConfig.getConnection()) {
      new CardService(connection).create(card);
    }
  }

  private void moveCard() throws SQLException {
    System.out.println("Informe o id do card que deseja mover para a próxima coluna");
    Long cardId = scanner.nextLong();
    List<BoardColumnInfoDTO> boardColumnsInfo = boardEntity.getBoardColumns().stream()
        .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
        .toList();
    try (Connection connection = ConnectionConfig.getConnection()) {
      new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  private void blockCard() throws SQLException {
    System.out.println("Informe o id do card que deseja bloquear");
    Long cardId = scanner.nextLong();
    System.out.println("Informe o motivo do bloqueio do card");
    String blockCause = scanner.next();
    List<BoardColumnInfoDTO> boardColumnsInfo = boardEntity.getBoardColumns().stream()
        .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
        .toList();
    try (Connection connection = ConnectionConfig.getConnection()) {
      new CardService(connection).block(cardId, blockCause, boardColumnsInfo);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  private void unblockCard() throws SQLException {
    System.out.println("Informe o id do card que deseja desbloquear");
    Long cardId = scanner.nextLong();
    System.out.println("Informe o motivo do desbloqueio do card");
    String unblockCause = scanner.next();
    try (Connection connection = ConnectionConfig.getConnection()) {
      new CardService(connection).unblock(cardId, unblockCause);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  private void cancelCard() throws SQLException {
    System.out.println("Informe o id do card que deseja cancelar");
    Long cardId = scanner.nextLong();
    List<BoardColumnInfoDTO> boardColumnsInfo = boardEntity.getBoardColumns().stream()
        .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
        .toList();
    BoardColumnEntity cancelColumn = boardEntity.getCancelColumn();
    try (Connection connection = ConnectionConfig.getConnection()) {
      new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
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

  private void showCard() throws SQLException {
    System.out.println("Informe o id co card que deseja Visualizar");
    Long selectedCardId = scanner.nextLong();
    try (Connection connection = ConnectionConfig.getConnection()) {
      Optional<CardDetailsDTO> card = new CardQuerryService(connection).findById(selectedCardId);
      card.ifPresentOrElse(c -> {
        System.out.println("Card %s - %s".formatted(c.id(), c.title()));
        System.out.println("nDescrição %s".formatted(c.description()));
        System.out.println(c.blocked() ? "O card está bolqueado com o motivo: %s".formatted(c.blockCause())
            : "O card não está bloqueado");
        System.out.println("O card já foi bloqueado %s vezes".formatted(c.blockAmount()));
        System.out.println("Está atualmente na coluna %s - %s".formatted(c.columnId(), c.columnName()));
      },
          () -> System.out.println("Não existe um card com o id %s selecionado".formatted(selectedCardId)));
    }
  }
}
