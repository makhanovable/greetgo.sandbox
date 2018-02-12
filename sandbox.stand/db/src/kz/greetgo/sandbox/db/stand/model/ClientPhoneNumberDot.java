package kz.greetgo.sandbox.db.stand.model;


import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;

public class ClientPhoneNumberDot {
  public String client;
  public String number;
  public PhoneNumberType type;

  public ClientPhoneNumberDot() {

  }

  public ClientPhoneNumberDot(String clientId, ClientPhoneNumber clientPhoneNumber) {
    this.client = clientId;
    this.type = clientPhoneNumber.type;
    this.number = clientPhoneNumber.number;
  }

  public ClientPhoneNumber toClientPhoneNumber() {
    ClientPhoneNumber clientPhoneNumber = new ClientPhoneNumber();
    clientPhoneNumber.client = client;
    clientPhoneNumber.number = number;
    clientPhoneNumber.type = type;
    return clientPhoneNumber;
  }
}
