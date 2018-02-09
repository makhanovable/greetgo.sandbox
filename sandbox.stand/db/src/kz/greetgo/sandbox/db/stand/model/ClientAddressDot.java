package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;

public class ClientAddressDot {
  public int clientId;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  public ClientAddressDot() {

  }

  public ClientAddressDot(int clientId, ClientAddress clientAddress) {
    this.clientId = clientId;
    this.flat = clientAddress.flat;
    this.street = clientAddress.street;
    this.house = clientAddress.house;
    this.type = clientAddress.type;
  }

  public ClientAddress toClientAddress() {
    ClientAddress clientAddress = new ClientAddress();
    clientAddress.type = type;
    clientAddress.flat = flat;
    clientAddress.house = house;
    clientAddress.clientId = clientId;
    clientAddress.street = street;
    return clientAddress;
  }
}
