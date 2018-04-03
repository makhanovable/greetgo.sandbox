package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientToSave {
  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public String birth_date;
  public String charm;
  public String fAdressStreet;
  public String fAdressHouse;
  public String fAdressFlat;
  public String rAdressStreet;
  public String rAdressHouse;
  public String rAdressFlat;
  public List<String> homePhone = new ArrayList<>();
  public List<String> workPhone = new ArrayList<>();
  public List<String> mobilePhones = new ArrayList<>();
}
