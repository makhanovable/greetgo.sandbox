package kz.greetgo.sandbox.db.register_impl.migration.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_ACCOUNT;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_TRANSACTION;

public class FrsParser implements AutoCloseable {


  private final Logger logger = Logger.getLogger(getClass());
  private Connection connection;
  private Map<TmpTableName, String> tableNames;

  private JsonParser parser = new JsonParser();
  private IdGenerator idGenerator;

  private PreparedStatement transactionPS;
  private PreparedStatement accountPS;

  private int batchCount = 0;
  private int maxBatchSize;
  private final Boolean originalAutoCommit;

  private JsonObject object;

  public FrsParser(IdGenerator idGenerator, int maxBatchSize, Connection connection, Map<TmpTableName, String> tableNames) throws Exception {
    this.idGenerator = idGenerator;
    this.maxBatchSize = maxBatchSize;
    this.connection = connection;
    this.tableNames = tableNames;
    this.originalAutoCommit = this.connection.getAutoCommit();
    this.connection.setAutoCommit(false);

    initPreparedStatements();
  }

  private void initPreparedStatements() throws SQLException {
    @SuppressWarnings("SqlResolve")
    String insertAccount = "INSERT INTO " + TMP_ACCOUNT.code + " (id, client_id, account_number, registeredAt) VALUES " +
      "(?, ?, ?, ?)";
    @SuppressWarnings("SqlResolve")
    String insertTransaction = "INSERT INTO " + TMP_TRANSACTION.code + " (id, account_number, money, finished_at, type) VALUES " +
      "(?, ?, ?, ?, ?)";

    insertAccount = insertAccount.replace(TMP_ACCOUNT.code, tableNames.get(TMP_ACCOUNT));
    insertTransaction = insertTransaction.replace(TMP_TRANSACTION.code, tableNames.get(TMP_TRANSACTION));

    accountPS = connection.prepareStatement(insertAccount);
    transactionPS = connection.prepareStatement(insertTransaction);
  }

  private void addBatch() throws SQLException {

    int index = 1;
    //JsonObject::get return Null if no such member exists.
    JsonElement elem = object.get("type");

    String type = elem != null ? elem.getAsString() : null;
    if ("new_account".equals(type)) {
      accountPS.setObject(index++, idGenerator.newId());
      accountPS.setObject(index++, getJsonField("client_id"));
      accountPS.setObject(index++, getJsonField("account_number"));
      accountPS.setObject(index, getJsonField("registered_at"));
      accountPS.addBatch();

    } else if ("transaction".equals(type)) {
      transactionPS.setObject(index++, idGenerator.newId());
      transactionPS.setObject(index++, getJsonField("account_number"));
      transactionPS.setObject(index++, getJsonField("money"));
      transactionPS.setObject(index++, getJsonField("finished_at"));
      transactionPS.setObject(index, getJsonField("transaction_type"));
      transactionPS.addBatch();

    } else {
      logger.fatal("line with unsupported frs type found: " + type);
      return;
    }

    if (++batchCount >= maxBatchSize) {
      commitAll();
      batchCount = 0;
    }
  }

  public void parse(String line) {

    object = parser.parse(line).getAsJsonObject();

    try {
      addBatch();
    } catch (Exception e) {
      logger.fatal("trying to add batch parsed frs line", e);
    }

  }

  private String getJsonField(String key) {
    JsonElement elem = object.get(key);
    if (elem == null)
      return null;
    String field = elem.getAsString();
    return "null".equals(field) ? null : field;
  }

  @Override
  public void close() throws Exception {
    commitAll();

    accountPS.close();
    transactionPS.close();
    connection.setAutoCommit(this.originalAutoCommit);
  }

  private void commitAll() throws SQLException {
    accountPS.executeBatch();
    transactionPS.executeBatch();
    connection.commit();
  }


}
