package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class ClientInfo {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Date birthDate;
  public int charm;
  public List<ClientAddress> addresses;
  public List<ClientPhone> phones;
}
