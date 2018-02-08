package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

public class ClientAccountTransaction {
  public int id;
  public int accauntId;
  public float money;
  public Timestamp finishedAt;
  public int typeId;
}
