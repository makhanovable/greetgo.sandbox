package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.model.Charm;

import java.util.Date;

public class ClientDot {
    public int id;
    public String surname;
    public String name;
    public String patronymic;
    public Date birthDate;
    public GenderType gender;
    public int charmId;
}
