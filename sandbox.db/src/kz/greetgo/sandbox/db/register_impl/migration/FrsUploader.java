package kz.greetgo.sandbox.db.register_impl.migration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.migration.error.ErrorFile;
import kz.greetgo.sandbox.db.register_impl.migration.error.ParsingValueException;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientAccountData;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientAccountTransactionData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FrsUploader {
  public Connection connection;
  public int maxBatchSize;
  public String clientAccountTable;
  public String clientAccountTransactionTable;

  private long curFileNo = 0;
  public ErrorFile errorFileWriter;

  private PreparedStatement clientAccountPreparedStatement;
  private PreparedStatement clientAccountTransactionPreparedStatement;

  private void prepare() throws SQLException {
    clientAccountPreparedStatement = connection.prepareStatement(
      "INSERT INTO " + clientAccountTable + " (record_no, cia_id, account_number, registered_at) " +
        "VALUES(?, ?, ?, ?)");

    clientAccountTransactionPreparedStatement = connection.prepareStatement(
      "INSERT INTO " + clientAccountTransactionTable +
        " (record_no, money, finished_at, transaction_type, account_number) " +
        "VALUES(?, ?, ?, ?, ?)");
  }

  private int curClientAccountRecordNum = 0;
  private int curClientAccountTransactionRecordNum = 0;

  private ClientAccountData clientAccountData;
  private ClientAccountTransactionData clientAccountTransactionData;

  private void setClientAccountPrepareStatement(Timestamp registeredAt)
    throws SQLException {
    int idx = 1;
    clientAccountPreparedStatement.setInt(idx++, curClientAccountRecordNum);
    clientAccountPreparedStatement.setString(idx++, clientAccountData.client_id);
    clientAccountPreparedStatement.setString(idx++, clientAccountData.account_number);
    clientAccountPreparedStatement.setTimestamp(idx, registeredAt);
  }

  private void setClientAccountTransactionPrepareStatement(BigDecimal money, Timestamp finishedAt) throws SQLException {
    int idx = 1;
    clientAccountTransactionPreparedStatement.setInt(idx++, curClientAccountTransactionRecordNum);
    clientAccountTransactionPreparedStatement.setBigDecimal(idx++, money);
    clientAccountTransactionPreparedStatement.setTimestamp(idx++, finishedAt);
    clientAccountTransactionPreparedStatement.setString(idx++, clientAccountTransactionData.transaction_type);
    clientAccountTransactionPreparedStatement.setString(idx, clientAccountTransactionData.account_number);
  }

  void parse(InputStream is) throws Exception {
    JsonFactory jsonFactory = new JsonFactory();

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
      String line, field, value;
      ObjectMapper objectMapper = new ObjectMapper();
      JsonToken token;

      prepare();

      while ((line = bufferedReader.readLine()) != null) {
        curFileNo++;
        try (JsonParser parser = jsonFactory.createParser(line)) {
          while ((token = parser.nextToken()) != null) {
            field = parser.getCurrentName();

            if (JsonToken.FIELD_NAME.equals(token) && "type".equals(field)) {
              parser.nextToken();
              value = parser.getValueAsString();

              if ("transaction".equals(value)) {
                clientAccountTransactionData = objectMapper.readValue(line, ClientAccountTransactionData.class);
                curClientAccountTransactionRecordNum++;

                try {
                  this.addClientAccountTransactionToBatch();
                } catch (ParsingValueException e) {
                  errorFileWriter.appendErrorLine(e.getMessage());
                }
              } else if ("new_account".equals(value)) {
                clientAccountData = objectMapper.readValue(line, ClientAccountData.class);
                curClientAccountRecordNum++;

                try {
                  this.addClientAccountToBatch();
                } catch (ParsingValueException e) {
                  errorFileWriter.appendErrorLine(e.getMessage());
                }
              } else {
                errorFileWriter.appendErrorLine("Неизвестная команда " + value);
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
      errorFileWriter.appendErrorLine(e.getMessage());
    }
  }

  // TODO: поменять все типы на int? bigint & long могут быть лишними
  int curClientAccountBatchCount = 0;
  int curClientAccountTransactionBatchCount = 0;

  private SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  private void addClientAccountToBatch() throws Exception {
    Timestamp registeredAt;

    try {
      registeredAt = new Timestamp(timestampDateFormat
        .parse(clientAccountData.registered_at.replace("T", " ")).getTime());
    } catch (NumberFormatException | ParseException e) {
      throw new ParsingValueException("Неправильный формат временного штампа registered_at у account_number = " +
        clientAccountData.client_id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    long age = Util.getAge(registeredAt.toLocalDateTime());
    if (age > 1000 || age < -1)
      throw new ParsingValueException("Значение registered_at выходит за рамки у account_number = " +
        clientAccountTransactionData.account_number + ".Возраст должен быть между -1 и 1000 годами");

    setClientAccountPrepareStatement(registeredAt);
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
      money = new BigDecimal(clientAccountTransactionData.money.replaceAll("_", ""));
    } catch (NumberFormatException e) {
      throw new ParsingValueException("Неправильный формат денег money у account_number = " +
        clientAccountTransactionData.account_number);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    try {
      finishedAt = new Timestamp(timestampDateFormat
        .parse(clientAccountTransactionData.finished_at.replace("T", " ")).getTime());
    } catch (IllegalArgumentException | ParseException e) {
      throw new ParsingValueException("Неправильный формат временного штампа finished_at у account_number = " +
        clientAccountTransactionData.account_number);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }//TODO: length 0 errors?

    long age = Util.getAge(finishedAt.toLocalDateTime());
    if (age > 1000 || age < -1)
      throw new ParsingValueException("Значение finished_at выходит за рамки у account_number = " +
        clientAccountTransactionData.account_number + ".Возраст должен быть между -1 и 1000 годами");

    setClientAccountTransactionPrepareStatement(money, finishedAt);
    clientAccountTransactionPreparedStatement.addBatch();

    curClientAccountTransactionBatchCount++;
    if (curClientAccountTransactionBatchCount > maxBatchSize) {
      clientAccountTransactionPreparedStatement.executeBatch();
      connection.commit();

      curClientAccountTransactionBatchCount = 0;
    }
  }
}
