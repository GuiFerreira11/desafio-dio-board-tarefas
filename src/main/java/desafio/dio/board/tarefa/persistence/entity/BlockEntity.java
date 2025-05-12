package desafio.dio.board.tarefa.persistence.entity;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class BlockEntity {

  private Long id;
  private String blockCause;
  private OffsetDateTime blockedAt;
  private String unblockCause;
  private OffsetDateTime unblockedAt;

}
