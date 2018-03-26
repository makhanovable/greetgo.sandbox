package kz.greetgo.sandbox.db.test.model;

import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;

import java.util.List;

public class ClientCia {
  public String id;
  public String cia_id;
  public String name;
  public String surname;
  public String patronymic;
  public String birthDate;
  public GenderType gender;
  public String charm;
  public String error;
  public ClientAddress actualAddress;
  public ClientAddress registerAddress;
  public List<ClientPhoneNumber> phoneNumbers;
}
