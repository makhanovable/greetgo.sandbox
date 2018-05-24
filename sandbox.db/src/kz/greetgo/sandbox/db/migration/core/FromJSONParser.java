package kz.greetgo.sandbox.db.migration.core;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class FromJSONParser {
    PreparedStatement accPS, transPS;
    Connection connection;

    int accBatchSize, transBatchSize;
    int MAX_BATCH_SIZE;
    int recordsCount = 0;

    public void execute (Connection connection, PreparedStatement accPS, PreparedStatement transPS, int maxBatchSize) {
        this.accPS = accPS;
        this.transPS = transPS;
        this.MAX_BATCH_SIZE = maxBatchSize;
        this.connection = connection;
    }

    public int parseRecordData(File file) throws Exception {

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            for(String line; (line = br.readLine()) != null; ) {
                JSONObject obj = new JSONObject (line);

                if ("transaction".equals(obj.getString("type"))) {

                    String money = obj.getString("money");
                    money = money.replaceAll("_","");
                    this.transPS.setFloat(1, Float.valueOf(money));
                    this.transPS.setString(2, obj.getString("account_number"));
                    String tstmp = obj.getString("finished_at");
                    tstmp = tstmp.replaceAll("T", " ");
                    this.transPS.setTimestamp(3, Timestamp.valueOf(tstmp));
                    this.transPS.setString(4, obj.getString("transaction_type"));

                    transBatchSize++;

                } else
                if ("new_account".equals(obj.getString("type"))) {

                    this.accPS.setString(1, obj.getString("account_number"));
                    String tstmp = obj.getString("registered_at");
                    tstmp = tstmp.replaceAll("T", " ");
                    this.accPS.setTimestamp(2, Timestamp.valueOf(tstmp));
                    this.accPS.setString(3, obj.getString("client_id"));

                    accBatchSize++;
                }

                recordsCount++;

                if (transBatchSize > MAX_BATCH_SIZE || accBatchSize > MAX_BATCH_SIZE) {
                    transPS.executeBatch();
                    accPS.executeBatch();
                    connection.commit();

                    transBatchSize = 0;
                    accBatchSize = 0;
                }
            }
        }

        return recordsCount;
    }

}
