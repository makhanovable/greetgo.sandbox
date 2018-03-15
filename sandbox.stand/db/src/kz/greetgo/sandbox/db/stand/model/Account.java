package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Account {
    public String id;
    public String clientID;
    public Float money;
    public String number;
    public Timestamp registered_at;

    public float getMinCash(Map<String, Transaction> transactions) {
        float minCash = this.money;
        float cash = 0;
        for (Transaction tr : transactions.values()) {
            if (tr.accountID.equals(this.id)) {
                cash += tr.money;
                if (cash < minCash) minCash = cash;
            }
        }

        return minCash;
    }

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
