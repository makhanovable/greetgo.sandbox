package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.AddressType;

public class AddressDot {
  public int id;
  public int clientId;
  public AddressType addressType;
  public String street;
  public String house;
  public String flat;

  public Address toAddress() {
    return new Address(id,clientId,addressType,street,house,flat);
  }

  public AddressDot() { }

  public AddressDot(int id, int clientId, AddressType addressType, String street, String house, String flat) {
    this.id = id;
    this.clientId = clientId;
    this.addressType = addressType;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}
