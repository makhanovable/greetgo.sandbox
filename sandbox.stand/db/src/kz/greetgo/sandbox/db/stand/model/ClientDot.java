package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.tools.AgeCalculator;

import java.util.Date;

public class ClientDot {

  private BeanGetter<StandDb> db;

  public int id;
  public String surname;
  public String name;
  public String patronymic;
  public Date birthDate;
  public GenderType gender;
  public int charmId;

  public String getFIO() {
    return new StringBuilder(this.name).append(this.surname).append(this.patronymic).toString();
  }

  public ClientDot() {

  }

  public ClientDot(ClientForm clientForm) {
    this.id = clientForm.id;
    this.name = clientForm.name;
    this.surname = clientForm.surname;
    this.gender = clientForm.gender;
    this.patronymic = clientForm.patronymic;
    this.birthDate = clientForm.birthDate;
    this.charmId = clientForm.charmId;
  }

  public ClientInfo toClientInfo() {
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.id = this.id;
    clientInfo.name = this.name;
    clientInfo.surname = this.surname;
    clientInfo.patronymic = this.patronymic;
    clientInfo.charmId = this.charmId;
    clientInfo.age = AgeCalculator.calculateAge(this.birthDate, new Date());

    return clientInfo;
  }

  public ClientForm toClientForm() {
    ClientForm clientForm = new ClientForm();
    clientForm.id = this.id;
    clientForm.name = this.name;
    clientForm.surname = this.surname;
    clientForm.patronymic = this.patronymic;
    clientForm.charmId = this.charmId;
    clientForm.birthDate = this.birthDate;
    clientForm.gender = gender;
    return clientForm;
  }


}


