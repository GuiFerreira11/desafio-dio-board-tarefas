package desafio.dio.board.tarefa.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class BoardColumnEntity {

  private Long id;
  private String name;
  private int order;
  private BoardColumnKindEnum kind;
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private BoardEntity board = new BoardEntity();
  private List<CardEntity> cards = new ArrayList<>();

}
