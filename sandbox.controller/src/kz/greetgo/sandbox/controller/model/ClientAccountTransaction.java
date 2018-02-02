package kz.greetgo.sandbox.controller.model;

import java.sql.Timestamp;

/**
 * Created by damze on 2/1/18.
 */
public class ClientAccountTransaction {
    private int id;
    private int accauntId;
    private float money;
    private Timestamp finishedAt;
    private int typeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccauntId() {
        return accauntId;
    }

    public void setAccauntId(int accauntId) {
        this.accauntId = accauntId;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
