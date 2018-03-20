package kz.greetgo.sandbox.db.register_impl.migration.handler;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.db.register_impl.IdGenerator;
import kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName;
import kz.greetgo.sandbox.db.register_impl.migration.model.AddressCia;
import kz.greetgo.sandbox.db.register_impl.migration.model.ClientCia;
import kz.greetgo.sandbox.db.register_impl.migration.model.PhoneCia;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_ADDRESS;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_CLIENT;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.TMP_PHONE;

public class CiaHandler extends DefaultHandler implements AutoCloseable {

  private final Logger logger = Logger.getLogger(getClass());

  private Connection connection;
  private Map<TmpTableName, String> tableNames;

  private IdGenerator idGenerator;


  private int maxBatchSize;

  private ClientCia client;

  private PreparedStatement clientPS;
  private PreparedStatement addrPS;
  private PreparedStatement phonePS;

  private int batchCount = 0;
  private final Boolean originalAutoCommit;

  private String content;

  public CiaHandler(IdGenerator idGenerator, int maxBatchSize, Connection connection, Map<TmpTableName, String> tableNames) throws SQLException {
    this.idGenerator = idGenerator;
    this.maxBatchSize = maxBatchSize;
    this.connection = connection;
    this.tableNames = tableNames;

    this.originalAutoCommit = this.connection.getAutoCommit();
    this.connection.setAutoCommit(false);

    initPreparedStatements();
    client = getClientCiaInstance(idGenerator.newId());
  }

  private ClientCia getClientCiaInstance(String id) {
    return new ClientCia(id);
  }

  private void initPreparedStatements() throws SQLException {
    @SuppressWarnings("SqlResolve")
    String insertClient = "INSERT INTO " + TMP_CLIENT.code + " (id, cia_id, name, surname, patronymic, gender, birthDate, charm) VALUES " +
      "(?, ?, ?, ?, ?, ?, ?, ?)";

    @SuppressWarnings({"SqlResolve"})
    String insertAddr = "INSERT INTO " + TMP_ADDRESS.code + " (client_id, type, street, house, flat) VALUES " +
      "(?, ?, ?, ?, ?)";

    @SuppressWarnings({"SqlResolve"})
    String insertPhone = "INSERT INTO " + TMP_PHONE.code + " (client_id, number, type) VALUES " +
      "(?, ?, ?)";

    insertClient = insertClient.replace(TMP_CLIENT.code, tableNames.get(TMP_CLIENT));
    insertAddr = insertAddr.replace(TMP_ADDRESS.code, tableNames.get(TMP_ADDRESS));
    insertPhone = insertPhone.replace(TMP_PHONE.code, tableNames.get(TMP_PHONE));

    clientPS = connection.prepareStatement(insertClient);
    addrPS = connection.prepareStatement(insertAddr);
    phonePS = connection.prepareStatement(insertPhone);
  }

  private void addBatch() {
    try {
      int index = 1;
      clientPS.setObject(index++, client.id);
      clientPS.setObject(index++, client.cia_id);
      clientPS.setObject(index++, client.name);
      clientPS.setObject(index++, client.surname);
      clientPS.setObject(index++, client.patronymic);
      clientPS.setObject(index++, client.gender);
      clientPS.setObject(index++, client.birth);
      clientPS.setObject(index, client.charm);

      clientPS.addBatch();

      if (client.reg != null) {
        index = 1;
        addrPS.setObject(index++, client.id);
        addrPS.setObject(index++, client.reg.type.toString());
        addrPS.setObject(index++, client.reg.street);
        addrPS.setObject(index++, client.reg.house);
        addrPS.setObject(index, client.reg.flat);

        addrPS.addBatch();
        batchCount++;
      }
      if (client.fact != null) {
        index = 1;
        addrPS.setObject(index++, client.id);
        addrPS.setObject(index++, client.fact.type.toString());
        addrPS.setObject(index++, client.fact.street);
        addrPS.setObject(index++, client.fact.house);
        addrPS.setObject(index, client.fact.flat);

        addrPS.addBatch();
        batchCount++;
      }

      for (PhoneCia phoneCia : client.numbers) {
        index = 1;
        phonePS.setObject(index++, client.id);
        phonePS.setObject(index++, phoneCia.number);
        phonePS.setObject(index, phoneCia.type.toString());

        phonePS.addBatch();
        batchCount++;
      }

      if (batchCount >= maxBatchSize) {
        commitAll();
        batchCount = 0;
      }

    } catch (SQLException e) {
      logger.trace(e);
    }

    client = getClientCiaInstance(idGenerator.newId());
  }


  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if ("client".equals(qName)) {
      client.cia_id = attributes.getValue("id");

    } else if ("name".equals(qName)) {
      client.name = attributes.getValue("value");

    } else if ("surname".equals(qName)) {
      client.surname = attributes.getValue("value");

    } else if ("patronymic".equals(qName)) {
      client.patronymic = attributes.getValue("value");

    } else if ("gender".equals(qName)) {
      client.gender = attributes.getValue("value");

    } else if ("charm".equals(qName)) {
      client.charm = attributes.getValue("value");

    } else if ("birth".equals(qName)) {
      client.birth = attributes.getValue("value");
    } else if ("register".equals(qName)) {
      client.reg = new AddressCia();
      client.reg.street = attributes.getValue("street");
      client.reg.house = attributes.getValue("house");
      client.reg.flat = attributes.getValue("flat");
      client.reg.type = AddressType.REG;

    } else if ("fact".equals(qName)) {
      client.fact = new AddressCia();
      client.fact.street = attributes.getValue("street");
      client.fact.house = attributes.getValue("house");
      client.fact.flat = attributes.getValue("flat");
      client.fact.type = AddressType.FACT;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("client".equals(qName)) {
      addBatch();
    } else if ("workPhone".equals(qName)) {
      PhoneCia number = new PhoneCia();
      number.number = content;
      number.type = PhoneNumberType.WORK;
      client.numbers.add(number);
    } else if ("homePhone".equals(qName)) {
      PhoneCia number = new PhoneCia();
      number.number = content;
      number.type = PhoneNumberType.HOME;
      client.numbers.add(number);
    } else if ("mobilePhone".equals(qName)) {
      PhoneCia number = new PhoneCia();
      number.number = content;
      number.type = PhoneNumberType.MOBILE;
      client.numbers.add(number);
    }
  }


  @Override
  public void characters(char ch[], int start, int length) throws SAXException {
    content = new String(ch, start, length);
  }

  @Override
  public void close() throws Exception {
    if (batchCount != 0)
      commitAll();

    clientPS.close();
    addrPS.close();
    phonePS.close();
    clear();
    connection.setAutoCommit(this.originalAutoCommit);
  }

  private void commitAll() throws SQLException {
    clientPS.executeBatch();
    addrPS.executeBatch();
    phonePS.executeBatch();
    connection.commit();
    clear();
  }

  private void clear() throws SQLException {
    clientPS.clearBatch();
    addrPS.clearBatch();
    phonePS.clearBatch();
  }


  @Override
  public void startDocument() throws SAXException {}

  @Override
  public void endDocument() throws SAXException {}

}
