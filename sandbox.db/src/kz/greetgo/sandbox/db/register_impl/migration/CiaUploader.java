package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientAddressData;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientData;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientPhoneData;
import org.xml.sax.Attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CiaUploader extends CommonSaxHandler {
  public Connection connection;
  public int maxBatchSize;
  public String clientTable;
  public String clientAddressTable;
  public String clientPhoneTable;

  private PreparedStatement clientPrepareStatement;
  private PreparedStatement clientAddressPrepareStatement;
  private PreparedStatement clientPhonePrepareStatement;

  private int curClientRecordNum = 0;
  private int curClientAddressRecordNum = 0;
  private int curClientPhoneRecordNum = 0;

  private void prepare() throws SQLException {
    clientPrepareStatement = connection.prepareStatement(
      "INSERT INTO " + clientTable + " (record_no, cia_id, surname, name, patronymic, gender, birth_date, charm_name) " +
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)"
    );

    clientAddressPrepareStatement = connection.prepareStatement(
      "INSERT INTO " + clientAddressTable + " (record_no, client_record_no, type, street, house, flat) " +
        "VALUES(?, ?, ?, ?, ?, ?)"
    );

    clientPhonePrepareStatement = connection.prepareStatement(
      "INSERT INTO " + clientPhoneTable + " (record_no, client_record_no, number, type) " +
        "VALUES(?, ?, ?, ?)"
    );
  }

  private ClientData clientData;
  private ClientAddressData clientAddressData;
  private ClientPhoneData clientPhoneData;

  private void setClientPrepareStatement() throws SQLException {
    int idx = 1;
    clientPrepareStatement.setInt(idx++, curClientRecordNum);
    clientPrepareStatement.setString(idx++, clientData.ciaId);
    clientPrepareStatement.setString(idx++, clientData.surname);
    clientPrepareStatement.setString(idx++, clientData.name);
    clientPrepareStatement.setString(idx++, clientData.patronymic);
    clientPrepareStatement.setString(idx++, clientData.gender);
    clientPrepareStatement.setString(idx++, clientData.birthdate);
    clientPrepareStatement.setString(idx, clientData.charmName);
  }

  private void setClientAddressPrepareStatement() throws SQLException {
    int idx = 1;
    clientAddressPrepareStatement.setInt(idx++, curClientAddressRecordNum);
    clientAddressPrepareStatement.setInt(idx++, curClientRecordNum);
    clientAddressPrepareStatement.setString(idx++, clientAddressData.type);
    clientAddressPrepareStatement.setString(idx++, clientAddressData.street);
    clientAddressPrepareStatement.setString(idx++, clientAddressData.house);
    clientAddressPrepareStatement.setString(idx, clientAddressData.flat);
  }

  private void setClientPhonePrepareStatement() throws SQLException {
    int idx = 1;
    clientPhonePrepareStatement.setInt(idx++, curClientPhoneRecordNum);
    clientPhonePrepareStatement.setInt(idx++, curClientRecordNum);
    clientPhonePrepareStatement.setString(idx++, clientPhoneData.number);
    clientPhonePrepareStatement.setString(idx, clientPhoneData.type);
  }

  private static final String TAG_CIA = "/cia";
  private static final String TAG_CLIENT = TAG_CIA + "/client";
  private static final String TAG_CLIENT_SURNAME = TAG_CLIENT + "/surname";
  private static final String TAG_CLIENT_NAME = TAG_CLIENT + "/name";
  private static final String TAG_CLIENT_PATRONYMIC = TAG_CLIENT + "/patronymic";
  private static final String TAG_CLIENT_GENDER = TAG_CLIENT + "/gender";
  private static final String TAG_CLIENT_BIRTH = TAG_CLIENT + "/birth";
  private static final String TAG_CLIENT_CHARM = TAG_CLIENT + "/charm";
  private static final String TAG_CLIENT_ADDRESS = TAG_CLIENT + "/address";
  private static final String TAG_CLIENT_ADDRESS_FACTUAL = TAG_CLIENT_ADDRESS + "/fact";
  private static final String TAG_CLIENT_ADDRESS_REGISTRATION = TAG_CLIENT_ADDRESS + "/register";
  private static final String TAG_CLIENT_PHONE = "Phone";
  private static final String TAG_CLIENT_HOME_PHONE = TAG_CLIENT + "/homePhone";
  private static final String TAG_CLIENT_MOBILE_PHONE = TAG_CLIENT + "/mobilePhone";
  private static final String TAG_CLIENT_WORK_PHONE = TAG_CLIENT + "/workPhone";

  int curClientAddressBatchCount = 0;
  int curClientPhoneBatchCount = 0;

  @Override
  protected void startTag(Attributes attributes) throws Exception {
    String path = path();
    if (path.equals(TAG_CIA)) {
      prepare();
      return;
    }
    if (path.equals(TAG_CLIENT)) {
      clientData = new ClientData();
      clientData.ciaId = attributes.getValue("id");
      curClientRecordNum++;
      return;
    }
    if (path.equals(TAG_CLIENT_SURNAME)) {
      clientData.surname = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_NAME)) {
      clientData.name = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_PATRONYMIC)) {
      clientData.patronymic = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_GENDER)) {
      clientData.gender = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_BIRTH)) {
      clientData.birthdate = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_CHARM)) {
      clientData.charmName = attributes.getValue("value");
      return;
    }
    if (path.equals(TAG_CLIENT_ADDRESS_FACTUAL)) {
      clientAddressData = new ClientAddressData();
      clientAddressData.type = AddressType.FACTUAL.name();
      clientAddressData.street = attributes.getValue("street");
      clientAddressData.house = attributes.getValue("house");
      clientAddressData.flat = attributes.getValue("flat");
      curClientAddressRecordNum++;

      this.addClientAddressToBatch();
      return;
    }
    if (path.equals(TAG_CLIENT_ADDRESS_REGISTRATION)) {
      clientAddressData.clientRecordNo = curClientRecordNum;
      clientAddressData.type = AddressType.REGISTRATION.name();
      clientAddressData.street = attributes.getValue("street");
      clientAddressData.house = attributes.getValue("house");
      clientAddressData.flat = attributes.getValue("flat");
      curClientAddressRecordNum++;

      this.addClientAddressToBatch();
      return;
    }
  }

  int totalClientBatchCount = 0, curClientBatchCount = 0;

  @Override
  protected void endTag() throws Exception {
    String path = path();

    if (path.endsWith(TAG_CLIENT_PHONE)) {
      clientPhoneData = new ClientPhoneData();
      clientPhoneData.number = text();
      //TODO: model of sandbox should match clientGenerator's?
      switch (path) {
        case TAG_CLIENT_HOME_PHONE: {
          clientPhoneData.type = PhoneType.HOME.name();
          break;
        }
        case TAG_CLIENT_MOBILE_PHONE: {
          clientPhoneData.type = PhoneType.MOBILE.name();
          break;
        }
        case TAG_CLIENT_WORK_PHONE: {
          clientPhoneData.type = PhoneType.WORK.name();
          break;
        }
        default: {
          throw new RuntimeException("There is no such phone in model enum");
        }
      }
      curClientPhoneRecordNum++;

      this.addClientPhoneToBatch();
      return;
    }
    if (path.equals(TAG_CLIENT)) {
      this.addClientToBatch();
      return;
    }
    if (path.equals(TAG_CIA)) {
      boolean needCommit = false;

      if (curClientBatchCount > 0) {
        clientPrepareStatement.executeBatch();
        needCommit = true;
        curClientBatchCount = 0;
      }

      if (curClientAddressBatchCount > 0) {
        clientAddressPrepareStatement.executeBatch();
        needCommit = true;
        curClientBatchCount = 0;
      }

      if (curClientPhoneBatchCount > 0) {
        clientPhonePrepareStatement.executeBatch();
        needCommit = true;
        curClientPhoneBatchCount = 0;
      }

      if (needCommit)
        connection.commit();

      return;
    }
  }

  private void addClientToBatch() throws SQLException {
    setClientPrepareStatement();
    clientPrepareStatement.addBatch();

    totalClientBatchCount++;
    curClientBatchCount++;
    if (curClientBatchCount > maxBatchSize) {
      clientPrepareStatement.executeBatch();
      connection.commit();
      curClientBatchCount = 0;
    }
  }

  private void addClientAddressToBatch() throws SQLException {
    setClientAddressPrepareStatement();
    clientAddressPrepareStatement.addBatch();

    curClientAddressBatchCount++;
    if (curClientAddressBatchCount > maxBatchSize) {
      clientAddressPrepareStatement.executeBatch();
      connection.commit();
      curClientAddressBatchCount = 0;
    }
  }

  private void addClientPhoneToBatch() throws SQLException {
    setClientPhonePrepareStatement();
    clientPhonePrepareStatement.addBatch();

    curClientPhoneBatchCount++;
    if (curClientPhoneBatchCount > maxBatchSize) {
      clientPhonePrepareStatement.executeBatch();
      connection.commit();
      curClientPhoneBatchCount = 0;
    }
  }
}
