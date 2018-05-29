package kz.greetgo.sandbox.db.migration.model;

import java.sql.Timestamp;

public class TransactionJSONRecord {
    public float money;
    public String account_number;
    public Timestamp finished_at;
    public String transaction_type;
}
