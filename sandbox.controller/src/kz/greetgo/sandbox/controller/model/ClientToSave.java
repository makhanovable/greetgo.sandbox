package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientToSave {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Long birthDate;
  public int charmId;
  public Address factAddress;
  public Address regAddress;
  public List<Phone> phones;
}
