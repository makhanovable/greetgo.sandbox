package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;

public class ClientAccountTransactionDot {
    public int id;
    public int ClientAccountId;
    public float money;
    public Timestamp finishedAt;
    public int type;
}
