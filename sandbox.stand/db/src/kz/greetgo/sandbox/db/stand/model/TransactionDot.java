package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;

public class TransactionDot {
    public int id;
    public int accountID;
    public Float money;
    public Timestamp finished_at;
    public int transactionTypeID;
}
