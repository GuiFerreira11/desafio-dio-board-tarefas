package desafio.dio.board.tarefa.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnKindEnum {
  INITIAL,
  FINAL,
  CANCEL,
  PENDING;

  public static BoardColumnKindEnum findByName(final String name) {
    return Stream.of(BoardColumnKindEnum.values()).filter(b -> b.name().equalsIgnoreCase(name)).findFirst()
        .orElseThrow();
  }
}
