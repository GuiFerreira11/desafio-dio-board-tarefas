package desafio.dio.board.tarefa.service;

import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.FINAL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import desafio.dio.board.tarefa.exception.CardBlockedException;
import desafio.dio.board.tarefa.exception.EntityNotFoundException;
import desafio.dio.board.tarefa.exception.CardFinishedExecption;
import desafio.dio.board.tarefa.persistence.dao.BlockDAO;
import desafio.dio.board.tarefa.persistence.dao.CardDao;
import desafio.dio.board.tarefa.persistence.dto.BoardColumnInfoDTO;
import desafio.dio.board.tarefa.persistence.dto.CardDetailsDTO;
import desafio.dio.board.tarefa.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardService {

  private final Connection connection;

  public CardEntity create(final CardEntity entity) throws SQLException {
    try {

      CardDao dao = new CardDao(connection);
      dao.create(entity);
      connection.commit();
      return entity;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  public void moveToNextColumn(final Long cardId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
    try {
      CardDao dao = new CardDao(connection);
      Optional<CardDetailsDTO> optional = dao.findById(cardId);
      CardDetailsDTO dto = optional
          .orElseThrow(() -> new EntityNotFoundException("O card com id %s não foi encontrado".formatted(cardId)));
      if (dto.blocked()) {
        throw new CardBlockedException(
            "O card com id %s está bloqueado. É necessário desbloqueá-lo mara mover".formatted(cardId));
      }
      BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
          .filter(ci -> ci.id().equals(dto.columnId()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
      if (currentColumn.kind().equals(FINAL)) {
        throw new CardFinishedExecption("O card já foi finalizado");
      }
      BoardColumnInfoDTO nextColumn = boardColumnsInfo.stream()
          .filter(ci -> ci.order() == currentColumn.order() + 1)
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("O card está cancelado"));
      dao.moveToColumn(cardId, nextColumn.id());
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  public void cancel(final Long cardId, final Long cancelColumnId, List<BoardColumnInfoDTO> boardColumnsInfo)
      throws SQLException {
    try {
      CardDao dao = new CardDao(connection);
      Optional<CardDetailsDTO> optional = dao.findById(cardId);
      CardDetailsDTO dto = optional
          .orElseThrow(() -> new EntityNotFoundException("O card com id %s não foi encontrado".formatted(cardId)));
      if (dto.blocked()) {
        throw new CardBlockedException(
            "O card com id %s está bloqueado. É necessário desbloqueá-lo mara mover".formatted(cardId));
      }
      BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
          .filter(ci -> ci.id().equals(dto.columnId()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
      if (currentColumn.kind().equals(FINAL)) {
        throw new CardFinishedExecption("O card já foi finalizado");
      }
      boardColumnsInfo.stream()
          .filter(ci -> ci.order() == currentColumn.order() + 1)
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("O card está cancelado"));
      dao.moveToColumn(cardId, cancelColumnId);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  public void block(final Long cardId, final String cause, List<BoardColumnInfoDTO> boardColumnsInfo)
      throws SQLException {
    try {
      CardDao dao = new CardDao(connection);
      Optional<CardDetailsDTO> optional = dao.findById(cardId);
      CardDetailsDTO dto = optional
          .orElseThrow(() -> new EntityNotFoundException("O card com id %s não foi encontrado".formatted(cardId)));
      if (dto.blocked()) {
        throw new CardBlockedException(
            "O card com id %s já está bloqueado".formatted(cardId));
      }
      BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
          .filter(ci -> ci.id().equals(dto.columnId()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
      if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
        throw new IllegalStateException(
            "O card já está na coluna do tipo %s e não pode ser cancelado".formatted(currentColumn.kind()));
      }
      BlockDAO blockDAO = new BlockDAO(connection);
      blockDAO.block(cardId, cause);
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  public void unblock(final Long cardId, final String cause) throws SQLException {
    try {
      CardDao dao = new CardDao(connection);
      Optional<CardDetailsDTO> optional = dao.findById(cardId);
      CardDetailsDTO dto = optional
          .orElseThrow(() -> new EntityNotFoundException("O card com id %s não foi encontrado".formatted(cardId)));
      if (!dto.blocked()) {
        throw new CardBlockedException(
            "O card com id %s não está bloqueado".formatted(cardId));
      }
      BlockDAO blockDAO = new BlockDAO(connection);
      blockDAO.unblock(cardId, cause);
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

}
