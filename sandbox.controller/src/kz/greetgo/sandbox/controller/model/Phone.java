package kz.greetgo.sandbox.controller.model;

public class Phone {
  public int id;
  public int clientId;
  public String number;
  public PhoneType type;

  public Phone(int id, int clientId, String number, PhoneType type) {
    this.id = id;
    this.clientId = clientId;
    this.number = number;
    this.type = type;
  }

  public Phone() { }
}
