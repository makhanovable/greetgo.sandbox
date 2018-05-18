package kz.greetgo.sandbox.db.migration.model;

import java.util.ArrayList;
import java.util.List;

public class ClientXMLRecord {
    public long number;
    public String id;
    public String surname, name, patronymic;
    public String charm, gender;
    public java.sql.Date birthDate;
    public String fStreet, fHouse, fFlat;
    public String rStreet, rHouse, rFlat;
    public List<String> homePhones = new ArrayList<>();
    public List<String> workPhones = new ArrayList<>();
    public List<String> mobilePhones = new ArrayList<>();
}
