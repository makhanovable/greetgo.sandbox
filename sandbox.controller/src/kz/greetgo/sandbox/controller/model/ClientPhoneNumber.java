package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.PhoneNumberType;

public class ClientPhoneNumber {
  public String client;
  public String number;
  public PhoneNumberType type;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientPhoneNumber)) return false;

    ClientPhoneNumber that = (ClientPhoneNumber) o;

    //noinspection SimplifiableIfStatement
    if (number != null ? !number.equals(that.number) : that.number != null) return false;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = number != null ? number.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}
