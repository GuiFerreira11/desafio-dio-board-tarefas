package desafio.dio.board.tarefa.persistence.entity;

import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static desafio.dio.board.tarefa.persistence.entity.BoardColumnKindEnum.INITIAL;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BoardEntity {

  private Long id;
  private String name;
  private List<BoardColumnEntity> boardColumns = new ArrayList<>();

  public BoardColumnEntity getInitialColumn() {
    return getFilteredColumn(INITIAL);
  }

  public BoardColumnEntity getCancelColumn() {
    return getFilteredColumn(CANCEL);
  }

  private BoardColumnEntity getFilteredColumn(BoardColumnKindEnum kind) {
    return boardColumns.stream()
        .filter(bc -> bc.getKind().equals(kind))
        .findFirst()
        .orElseThrow();
  }

}
