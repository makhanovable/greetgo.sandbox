package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

public class Account {
  public int id;
  public int clientId;
  public Float money;
  public String number;
  public Timestamp registeredAt;

  public boolean isActive = true;

  public Account(int id, int clientId, Float money, String number, Timestamp registeredAt) {
    this.id = id;
    this.clientId = clientId;
    this.money = money;
    this.number = number;
    this.registeredAt = registeredAt;
  }
}
