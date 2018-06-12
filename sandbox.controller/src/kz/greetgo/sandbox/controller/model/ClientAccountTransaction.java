package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

public class ClientAccountTransaction {
    public int id;
    public int account;
    public float money;
    public Timestamp finished_at;
    public int type;
}
