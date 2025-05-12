package desafio.dio.board.tarefa.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class CardEntity {

  private Long id;
  private String title;
  private String description;
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private BoardColumnEntity boardColumn = new BoardColumnEntity();

}
