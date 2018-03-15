package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;

public class Transaction {
    public String id;
    public String accountID;
    public Float money;
    public Timestamp finished_at;
    public String transactionTypeID;
}
