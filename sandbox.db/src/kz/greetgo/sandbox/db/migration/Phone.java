package kz.greetgo.sandbox.db.migration;

public class Phone {
    public String type, number;

    @Override
    public String toString() {
        return "Phone{" +
                "type='" + type + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
