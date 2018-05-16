package kz.greetgo.sandbox.db.stand.model;

import java.util.Date;
import java.util.List;

public class ClientDot {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public Date birthDate;
  public int charm;
  public List<AddressDot> addresses;
  public List<PhoneDot> phones;
}
