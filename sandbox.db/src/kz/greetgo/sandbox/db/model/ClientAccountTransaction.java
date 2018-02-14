package kz.greetgo.sandbox.db.model;

import java.sql.Timestamp;

public class ClientAccountTransaction {
  public String id;
  public int accauntId;
  public float money;
  public Timestamp finishedAt;
  public int typeId;
}
