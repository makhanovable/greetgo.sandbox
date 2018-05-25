package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientDetails {
  public int id = -1;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Long birthDate;
  public int charmId;
  public List<Charm> charmsDictionary;
  public Address factAddress;
  public Address regAddress;
  public List<Phone> phones;
}
