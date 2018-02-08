package kz.greetgo.sandbox.db.stand.model;


import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;

public class ClientPhoneNumberDot {
  public int clientId;
  public String number;
  public PhoneNumberType type;

  public ClientPhoneNumberDot() {

  }

  public ClientPhoneNumberDot(int clientId, ClientPhoneNumber clientPhoneNumber) {
    this.clientId = clientId;
    this.type = clientPhoneNumber.type;
    this.number = clientPhoneNumber.number;
  }

  public ClientPhoneNumber toClientPhoneNumber() {
    ClientPhoneNumber clientPhoneNumber = new ClientPhoneNumber();
    clientPhoneNumber.clientId = clientId;
    clientPhoneNumber.number = number;
    clientPhoneNumber.type = type;
    return clientPhoneNumber;
  }
}
