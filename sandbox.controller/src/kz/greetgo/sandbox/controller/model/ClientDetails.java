package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientDetails {
    public String id;
    public String name;
    public String surname;
    public String patronymic;
    public String birth_date;
    public String gender;
    public String charm;
    public String fAdressStreet = "";
    public String fAdressHouse = "";
    public String fAdressFlat = "";
    public String rAdressStreet = "";
    public String rAdressHouse = "";
    public String rAdressFlat = "";
    public String workPhone = "";
    public String homePhone = "";
    public List<String> mobilePhones;

    public ClientDetails() {
        this.mobilePhones = new ArrayList<String>();
    }
}