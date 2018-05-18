package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.Gender;

import java.util.Date;

public class ClientDot {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public Date birthDate;
  public int charmId;

  public Client toClient() {
    return new Client(this.id,
                      this.name,
                      this.surname,
                      this.patronymic,
                      Gender.valueOf(this.gender),
                      this.birthDate,
                      this.charmId);
  }
}
