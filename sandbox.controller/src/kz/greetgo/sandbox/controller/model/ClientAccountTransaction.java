package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

public class ClientAccountTransaction {
    public Long id;
    public Long account;
    public float money;
    public Timestamp finished_at;
    public Long type;
}
