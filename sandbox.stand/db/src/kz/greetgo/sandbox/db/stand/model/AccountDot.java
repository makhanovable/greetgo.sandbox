package kz.greetgo.sandbox.db.stand.model;


import kz.greetgo.sandbox.controller.model.Account;

import java.sql.Timestamp;

public class AccountDot {
  public int id;
  public int clientId;
  public Float money;
  public String number;
  public Timestamp registeredAt;

  public Account toAccount() {
    return new Account(this.id,
                        this.clientId,
                        this.money,
                        this.number,
                        this.registeredAt);
  }
}
