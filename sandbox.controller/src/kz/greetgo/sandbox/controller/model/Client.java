package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class Client {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  public Long birthDate;
  public int charmId;

  public boolean isActive = true;

  public Client(int id, String name, String surname, String patronymic, Gender gender, Long birthDate, int charmId) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charmId = charmId;
  }

  public Client() { }
}
