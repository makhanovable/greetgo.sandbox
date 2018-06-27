package kz.greetgo.sandbox.db.migration;

public class Account {
    public String type;
    public String money;
    public String finished_at;
    public String transaction_type;
    public String account_number;

    public String client_id;
    public String registered_at;

    public Account() {
    }

    @Override
    public String toString() {
        return "Account{" +
                "type='" + type + '\'' +
                ", money='" + money + '\'' +
                ", finished_at='" + finished_at + '\'' +
                ", transaction_type='" + transaction_type + '\'' +
                ", account_number='" + account_number + '\'' +
                ", client_id='" + client_id + '\'' +
                ", registered_at='" + registered_at + '\'' +
                '}';
    }

}
