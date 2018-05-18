package kz.greetgo.sandbox.controller.model;

public class AccountInfo {
  public int id;
  public String fullName;
  public String charm;
  public int age;
  public float totalAccBalance;
  public float maxAccBalance;
  public float minAccBalance;

  public AccountInfo(int id, String fullName, String charm, int age, float totalAccBalance, float maxAccBalance, float minAccBalance) {
    this.id = id;
    this.fullName = fullName;
    this.charm = charm;
    this.age = age;
    this.totalAccBalance = totalAccBalance;
    this.maxAccBalance = maxAccBalance;
    this.minAccBalance = minAccBalance;
  }

  public AccountInfo(String fullName) {
    this.fullName = fullName;
  }

  public AccountInfo() {

  }
}
