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
}
