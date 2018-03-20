package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;
import java.util.*;

public class Account {
    public String id;
    public String clientID;
    public Float money;
    public String number;
    public Timestamp registered_at;

    // TODO: удалить, если не используешь
    public float getMinCash(Map<String, Transaction> transactions) {
        float minCash = this.money;
        float cash = 0;
        for (Transaction tr : transactions.values()) {
            if (Objects.equals(tr.accountID, this.id)) {
                cash += tr.money;
                if (cash < minCash) minCash = cash;
            }
        }

        return minCash;
    }

    // TODO: удалить, если не используешь
    public float getMaxCash(Map<String, Transaction> transactions) {
        float maxCash = 0;
        float cash = 0;
        for (Transaction tr : transactions.values()) {
            if (tr.accountID.equals(this.id)) {
                cash += tr.money;
                if (cash > maxCash) maxCash = cash;
            }
        }

        return maxCash;
    }
}
