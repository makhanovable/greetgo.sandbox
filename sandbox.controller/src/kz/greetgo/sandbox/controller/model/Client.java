package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class Client {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Date birthDate;
  public int charm;

  public Client(int id, String name, String surname, String patronymic, Gender gender, Date birthDate, int charm) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charm = charm;
  }
}
