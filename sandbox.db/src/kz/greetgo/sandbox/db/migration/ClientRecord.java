package kz.greetgo.sandbox.db.migration;

import java.util.Date;
import java.util.List;

public class ClientRecord {
    public String id;
    public String surname, name, patronymic;
    public Date birth;
    public String charm;
    public String gender;
    public List<Phone> phones;
    public List<Addr> addrs;
}
