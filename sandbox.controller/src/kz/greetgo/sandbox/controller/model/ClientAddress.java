package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.AddressType;

public class ClientAddress {
  public String client;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientAddress)) return false;

    ClientAddress that = (ClientAddress) o;

    if (type != that.type) return false;
    if (street != null ? !street.equals(that.street) : that.street != null) return false;
    if (house != null ? !house.equals(that.house) : that.house != null) return false;
    return flat != null ? flat.equals(that.flat) : that.flat == null;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (street != null ? street.hashCode() : 0);
    result = 31 * result + (house != null ? house.hashCode() : 0);
    result = 31 * result + (flat != null ? flat.hashCode() : 0);
    return result;
  }
}
