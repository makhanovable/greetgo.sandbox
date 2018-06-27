package kz.greetgo.sandbox.db.migration;

import java.util.List;

public class ClientRecord {
    public String id;
    public String surname, name, patronymic;
    public String birth;
    public String charm;
    public String gender;
    public List<Phone> phones;
    public List<Addr> addrs;

    @Override
    public String toString() {
        return "ClientRecord{" +
                "id='" + id + '\'' +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birth='" + birth + '\'' +
                ", charm='" + charm + '\'' +
                ", gender='" + gender + '\'' +
                ", phones=" + phones +
                ", addrs=" + addrs +
                '}';
    }
}
