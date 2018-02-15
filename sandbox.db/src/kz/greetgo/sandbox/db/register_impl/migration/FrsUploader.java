package kz.greetgo.sandbox.db.register_impl.migration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.register_impl.migration.error.ParsingValueException;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientAccountData;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientAccountTransactionData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FrsUploader {
  public Connection connection;
  public int maxBatchSize;
  public String clientAccountTable;
  public String clientAccountTransactionTable;

  private PreparedStatement clientAccountPreparedStatement;
  private PreparedStatement clientAccountTransactionPreparedStatement;

  private int curClientAccountRecordNum = 0;
  private int curClientAccountTransactionRecordNum = 0;

  private void prepare() throws SQLException {
    clientAccountPreparedStatement = connection.prepareStatement(
      "INSERT INTO " + clientAccountTable + " (record_no, client_id, account_number, registered_at) " +
        "VALUES(?, ?, ?, ?)");

    clientAccountTransactionPreparedStatement = connection.prepareStatement(
      "INSERT INTO " + clientAccountTransactionTable + " (record_no, money, finished_at, transaction_type, account_number) " +
        "VALUES(?, ?, ?, ?, ?)");
  }

  private ClientAccountData clientAccountData;
  private ClientAccountTransactionData clientAccountTransactionData;

  private void setClientAccountPrepareStatement(String clientId, String accountNumber, Timestamp registeredAt)
    throws SQLException {
    int idx = 1;
    clientAccountPreparedStatement.setInt(idx++, curClientAccountRecordNum);
    clientAccountPreparedStatement.setString(idx++, clientId);
    clientAccountPreparedStatement.setString(idx++, accountNumber);
    clientAccountPreparedStatement.setTimestamp(idx, registeredAt);
  }

  private void setClientAccountTransactionPrepareStatement(BigDecimal money, Timestamp finishedAt, String type,
                                                           String accountNum) throws SQLException {
    int idx = 1;
    clientAccountTransactionPreparedStatement.setInt(idx++, curClientAccountTransactionRecordNum);
    clientAccountTransactionPreparedStatement.setBigDecimal(idx++, money);
    clientAccountTransactionPreparedStatement.setTimestamp(idx++, finishedAt);
    clientAccountTransactionPreparedStatement.setString(idx++, type);
    clientAccountTransactionPreparedStatement.setString(idx, accountNum);
  }

  void parse(FileInputStream fileInputStream, BufferedWriter errorWriter) throws Exception {
    JsonFactory jsonFactory = new JsonFactory();

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {
      String line, field, value;
      ObjectMapper objectMapper = new ObjectMapper();
      JsonToken token;

      prepare();

      while ((line = bufferedReader.readLine()) != null) {
        try (JsonParser parser = jsonFactory.createParser(line)) {
          while ((token = parser.nextToken()) != null) {
            field = parser.getCurrentName();

            if (token.equals(JsonToken.FIELD_NAME) && field.equals("type")) {
              parser.nextToken();
              value = parser.getValueAsString();

              if (value.equals("transaction")) {
                clientAccountTransactionData = objectMapper.readValue(line, ClientAccountTransactionData.class);
                curClientAccountTransactionRecordNum++;

                try {
                  this.addClientAccountTransactionToBatch();
                } catch (ParsingValueException e) {
                  errorWriter.append(e.getMessage());
                }

                if (value.equals("new_account")) {
                  clientAccountData = objectMapper.readValue(line, ClientAccountData.class);
                  curClientAccountRecordNum++;
                  try {
                    this.addClientAccountToBatch();
                  } catch (ParsingValueException e) {
                    errorWriter.append(e.getMessage());
                  }
                }
              }
            }
          }
        }
      }

      boolean needCommit = false;

      if (curClientAccountBatchCount > 0) {
        clientAccountPreparedStatement.executeBatch();
        needCommit = true;
        curClientAccountBatchCount = 0;
      }

      if (curClientAccountTransactionBatchCount > 0) {
        clientAccountTransactionPreparedStatement.executeBatch();
        needCommit = true;
        curClientAccountTransactionBatchCount = 0;
      }

      if (needCommit)
        connection.commit();

      return;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      errorWriter.append(e.getMessage());
    }
  }

  // TODO: поменять все типы на int? bigint & long могут быть лишними
  int curClientAccountBatchCount = 0;
  int curClientAccountTransactionBatchCount = 0;

  private void addClientAccountToBatch() throws Exception {
    Timestamp registeredAt;

    try {
      registeredAt = Timestamp.valueOf(clientAccountData.registered_at);
    } catch (IllegalArgumentException e) {
      throw new ParsingValueException("Неправильный формат даты registered_at у account_number = " +
        clientAccountData.client_id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }


    setClientAccountPrepareStatement(clientAccountData.client_id, clientAccountData.account_number, registeredAt);
    clientAccountPreparedStatement.addBatch();

    curClientAccountBatchCount++;
    if (curClientAccountBatchCount > maxBatchSize) {
      clientAccountPreparedStatement.executeBatch();
      connection.commit();
      curClientAccountBatchCount = 0;
    }
  }

  private void addClientAccountTransactionToBatch() throws Exception {
    BigDecimal money;
    Timestamp finishedAt;

    try {
      money = new BigDecimal(clientAccountTransactionData.money);
    } catch (NumberFormatException e) {
      throw new ParsingValueException("");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    try {
      finishedAt = Timestamp.valueOf(clientAccountTransactionData.finished_at);
    } catch (IllegalArgumentException e) {
      throw new ParsingValueException("");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    setClientAccountTransactionPrepareStatement(
      money, finishedAt, clientAccountTransactionData.type, clientAccountTransactionData.account_number);
    clientAccountTransactionPreparedStatement.addBatch();

    curClientAccountTransactionBatchCount++;
    if (curClientAccountTransactionBatchCount > maxBatchSize) {
      clientAccountTransactionPreparedStatement.executeBatch();
      connection.commit();
      curClientAccountTransactionBatchCount = 0;
    }
  }
}
