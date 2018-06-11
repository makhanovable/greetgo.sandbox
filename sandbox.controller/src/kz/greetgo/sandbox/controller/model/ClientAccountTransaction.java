package kz.greetgo.sandbox.controller.model;

import java.security.Timestamp;

public class ClientAccountTransaction {
    public int id;
    public int account;
    public float money;
    public Timestamp finished_at;
    public int type;
}
