package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.util.Util;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MigrateOneCiaFile {
  public File inputFile;
  public File outputErrorFile;
  public int maxBatchSize = 100;
  public Connection connection;

  String tmpClientTableName;
  String tmpClientAddressTableName;
  String tmpClientPhoneTableName;
  String tmpCharmTableName;

  static final String clientTableNameToReplace = "client_to_replace";
  static final String clientAddressTableNameToReplace = "client_address_to_replace";
  static final String clientPhoneTableNameToReplace = "client_phone_to_replace";

  private static final String datePattern = "YYYY-MM-DD";

  public void migrate() throws Exception {
    prepareTmpTables();
    uploadData();
    processErrors();
    migrateData();
    downloadErrors();
  }

  void prepareTmpTables() throws SQLException {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String additionalId = sdf.format(now) + "_" + Util.generateRandomString(8);

    tmpClientTableName = "tmp_migration_client_" + additionalId;
    tmpClientAddressTableName = "tmp_migration_address_" + additionalId;
    tmpClientPhoneTableName = "tmp_migration_phone_" + additionalId;
    tmpCharmTableName = "tmp_migration_charm_" + additionalId;

    exec("CREATE TABLE " + clientTableNameToReplace + " (" +
      "  instance_id bigint NOT NULL, " +
      "  cia_id varchar(64), " +
      "  surname varchar(256), " +
      "  name varchar(256), " +
      "  patronymic varchar(128), " +
      "  gender varchar(16), " +
      "  charm_name varchar(128), " +
      "  charm_id int, " +
      "  birth_date varchar(16), " +
      "  status int NOT NULL DEFAULT 0, " +
      "  error varchar(256), " +
      "  PRIMARY KEY(instance_id)" +
      ")");

    exec("CREATE TABLE " + clientAddressTableNameToReplace + " (" +
      "  instance_id bigint NOT NULL, " +
      "  type varchar(64) NOT NULL, " +
      "  street varchar(128), " +
      "  house varchar(128), " +
      "  flat varchar(128), " +
      "  PRIMARY KEY(instance_id, type)" +
      ")");

    exec("CREATE TABLE " + clientPhoneTableNameToReplace + " (" +
      "  instance_id bigint NOT NULL, " +
      "  number varchar(64) NOT NULL, " +
      "  type varchar(128), " +
      "  PRIMARY KEY(instance_id, number)" +
      ")");
/*
    exec("CREATE TABLE charm_to_replace (" +
      "  id int NOT NULL, " +
      "  name varchar(128) NOT NULL, " +
      "  description varchar(128), " +
      "  energy real, " +
      "  PRIMARY KEY(id)" +
      ")");*/
  }

  void uploadData() throws Exception {
    connection.setAutoCommit(false);

    XMLReader reader = XMLReaderFactory.createXMLReader();

    CiaUploader ciaUploader = new CiaUploader();
    ciaUploader.connection = connection;
    ciaUploader.maxBatchSize = maxBatchSize;
    ciaUploader.clientTable = tmpClientTableName;
    ciaUploader.clientAddressTable = tmpClientAddressTableName;
    ciaUploader.clientPhoneTable = tmpClientPhoneTableName;
    reader.setContentHandler(ciaUploader);

    try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
      reader.parse(new InputSource(fileInputStream));
    }

    connection.setAutoCommit(true);
  }

  void processErrors() throws SQLException {
    //TODO: чтобы ускорить загрузку мб убрать проверку ошибки на нул?
    exec("UPDATE " + clientTableNameToReplace + " " +
      "SET error = 'Пустое значение surname у ciaId = '||cia_id " +
      "WHERE surname IS NULL OR length(trim(surname)) = 0 "
    );

    exec("UPDATE " + clientTableNameToReplace + " " +
      "SET error = " + "\'Пустое значение name у ciaId = \'" + "||cia_id " +
      "WHERE error IS NULL AND (name IS NULL OR length(trim(name)) = 0) "
    );

    exec("UPDATE " + clientTableNameToReplace + " " +
      "SET error = " + "\'Пустое значение birth у ciaId = \'" + "||cia_id " +
      "WHERE error IS NULL AND (birth_date IS NULL OR length(trim(birth_date)) = 0) "
    );

    exec("UPDATE " + clientTableNameToReplace + " " +
      "SET error = " + "\'Неправильное значение birth у ciaId = \'" + "||cia_id" +
      " ||\'. Возраст должен быть между 1000 и 3 годами\' " +
      "WHERE error IS NULL AND " +
      "  (extract(YEAR FROM age(to_date(birth_date, \'" + datePattern + "\'))) <= 3 OR " +
      "  extract(YEAR FROM age(to_date(birth_date, \'" + datePattern + "\'))) >= 1000) "
    );
  }

  void migrateData() throws SQLException {
    exec("INSERT INTO client SELECT * FROM " + clientTableNameToReplace + ", " + clientAddressTableNameToReplace + ")");
  }

  void downloadErrors() {

  }

  void exec(String sql) throws SQLException {
    sql = sql.replaceAll(clientTableNameToReplace, tmpClientTableName);
    sql = sql.replaceAll(clientAddressTableNameToReplace, tmpClientAddressTableName);
    sql = sql.replaceAll(clientPhoneTableNameToReplace, tmpClientPhoneTableName);

    System.out.println(sql);

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }
}

/*
TODO
status states (state machine)

 */
