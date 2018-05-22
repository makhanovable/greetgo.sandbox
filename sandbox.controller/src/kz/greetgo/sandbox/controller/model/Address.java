package kz.greetgo.sandbox.controller.model;

public class Address {
  public int id;
  public int clientId;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  public boolean isActive = true;

  public Address() { }

  public Address(int id, int clientId, AddressType type, String street, String house, String flat) {
    this.id = id;
    this.clientId = clientId;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}
