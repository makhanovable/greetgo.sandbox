package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientPhone;

public class ClientDetailsDot {
    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public String gender;
    public String birth_date;
    public int charm;
    public String addrFactStreet;
    public String addrFactHome;
    public String addrFactFlat;
    public String addrRegStreet;
    public String addrRegHome;
    public String addrRegFlat;
    public ClientPhone[] phones;
}