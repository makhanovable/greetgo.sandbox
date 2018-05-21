package kz.greetgo.sandbox.controller.model;

public class Address {
  public int id;
  public int clientId;
  public AddressType addressType;
  public String street;
  public String house;
  public String flat;

  public Address() { }

  public Address(int id, int clientId, AddressType addressType, String street, String house, String flat) {
    this.id = id;
    this.clientId = clientId;
    this.addressType = addressType;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}
