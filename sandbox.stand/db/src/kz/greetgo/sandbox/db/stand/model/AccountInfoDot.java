package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.AccountInfo;

public class AccountInfoDot {
  public int id;
  public String fullName;
  public String charm;
  public int age;
  public float totalAccBalance;
  public float maxAccBalance;
  public float minAccBalance;

  public AccountInfo toAccountInfo() {
    return new AccountInfo(this.id,
                            this.fullName,
                            this.charm,
                            this.age,
                            this.totalAccBalance,
                            this.maxAccBalance,
                            this.minAccBalance);
  }

  public void showInfo() {
    System.out.println("----------: Init AccountInfo " + fullName);
  }

}
