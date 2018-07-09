package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.model.Account;
import kz.greetgo.sandbox.db.migration.util.Insert;
import kz.greetgo.sandbox.db.migration.model.Transaction;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FRSParser {

    private Connection connection;
    private PreparedStatement accPS;
    private PreparedStatement trPS;
    private int batch = 0;
    private int maxBatchSize;

    FRSParser(Connection connection, int maxBatchSize) {
        this.connection = connection;
        this.maxBatchSize = maxBatchSize;
    }

    public void parseAndInsertToTempTables(String file) throws Exception {
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line;
        ObjectMapper mapper = new ObjectMapper();

        Insert acc = new Insert("TMP_ACC");
        acc.field(1, "cia_client_id", "?");
        acc.field(2, "account_number", "?");
        acc.field(3, "registered_at", "?");

        Insert tr = new Insert("TMP_TRANS");
        tr.field(1, "money", "?");
        tr.field(2, "account_number", "?");
        tr.field(3, "transaction_type", "?");
        tr.field(4, "finished_at", "?");

        accPS = connection.prepareStatement(acc.toString());
        trPS = connection.prepareStatement(tr.toString());

        while ((line = bf.readLine()) != null) {
            if (line.contains("new_account")) {
                Account account = mapper.readValue(line, Account.class);
                insertAccount(account);
            } else {
                Transaction transaction = mapper.readValue(line, Transaction.class);
                insertTransaction(transaction);
            }
        }
        if (batch > 0) {
            System.out.println("COMMIT " + batch);
            accPS.executeBatch();
            trPS.executeBatch();
            connection.commit();
        }
        accPS.close();
        trPS.close();
    }

    private void insertAccount(Account account) throws Exception {
        accPS.setString(1, account.client_id);
        accPS.setString(2, account.account_number);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date date = dateFormat.parse(account.registered_at.replace("T", " "));
        accPS.setTimestamp(3, new java.sql.Timestamp(date.getTime()));
        accPS.addBatch();
        execute();
    }

    private void insertTransaction(Transaction transaction) throws Exception {
        trPS.setDouble(1, Double.parseDouble(transaction.money.replace("_", "")));
        trPS.setString(2, transaction.account_number);
        trPS.setString(3, transaction.transaction_type);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date date = dateFormat.parse(transaction.finished_at.replace("T", " "));
        trPS.setTimestamp(4, new java.sql.Timestamp(date.getTime()));
        trPS.addBatch();
        execute();
    }

    private void execute() throws Exception {
        batch++;
        if (batch >= maxBatchSize) {
            accPS.executeBatch();
            trPS.executeBatch();
            connection.commit();
            System.out.println("COMMIT " + batch);
            batch = 0;
        }
    }

}
