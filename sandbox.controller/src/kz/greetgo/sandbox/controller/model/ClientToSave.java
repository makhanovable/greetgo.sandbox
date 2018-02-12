package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.GenderType;

import java.util.Date;
import java.util.List;

public class ClientToSave {
  public String id;
  public String surname;
  public String name;
  public String patronymic;
  public Date birthDate;
  public GenderType gender;
  public String charm;

  public ClientAddress actualAddress;
  public ClientAddress registerAddress;

  public List<ClientPhoneNumber> toSave;
  public List<ClientPhoneNumber> toDeleteNumbers;
}
