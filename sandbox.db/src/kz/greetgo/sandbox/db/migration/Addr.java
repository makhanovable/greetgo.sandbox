package kz.greetgo.sandbox.db.migration;

public class Addr {
    public String street, house, flat, type;

    @Override
    public String toString() {
        return "Addr{" +
                "street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", flat='" + flat + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
