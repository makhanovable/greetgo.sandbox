package kz.greetgo.sandbox.db.migration;

import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CIAMigration {

    private String path;
    private Connection connection;

    CIAMigration(Connection connection, String path) {
        this.connection = connection;
        this.path = path;
    }

    public void migrate() throws Exception {
        createTempTables();
        prepareData();
        validateTableData();
        migrateCharmTable();
        migrateClientTable();
        migrateClientAddressTable();
        migrateClientPhoneTable();
    }

    private void prepareData() throws Exception {
        connection.setAutoCommit(false);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        CIAParser parser = new CIAParser(connection);
        xmlReader.setContentHandler(parser);
        xmlReader.parse(path);
        connection.setAutoCommit(true);
    }

    private void createTempTables() {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_CLIENT CASCADE;" +
                "CREATE TABLE TMP_CLIENT (" +
                "   info VARCHAR(100)," +
                "   cia_id VARCHAR(100)," +
                "   client_id BIGINT DEFAULT NULL," +
                "   name VARCHAR(100)," +
                "   surname VARCHAR(100)," +
                "   patronymic VARCHAR(100)," +
                "   birth DATE," +
                "   charm VARCHAR(100)," +
                "   gender VARCHAR(100)," +
                "   num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ADDRESS CASCADE;" +
                "CREATE TABLE TMP_ADDRESS (" +
                "   type VARCHAR(50)," +
                "   street VARCHAR(100)," +
                "   house VARCHAR(50)," +
                "   flat VARCHAR(50)," +
                "   num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_PHONE CASCADE;" +
                "CREATE TABLE TMP_PHONE (" +
                "   info VARCHAR(100)," + // // TODO можно убрать и юзать join
                "   client_id BIGINT DEFAULT NULL," + // TODO можно убрать и юзать join
                "   type VARCHAR(50)," +
                "   phone VARCHAR(100)," +
                "   num BIGINT)");
    }

    private void validateTableData() {
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'SURNAME INVALID'" +
                "   WHERE info ISNULL AND (surname <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'NAME INVALID'" +
                "   WHERE info ISNULL AND (name <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'BIRTH_DATE INVALID'" +
                "   WHERE info ISNULL AND birth ISNULL");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'CHARM INVALID' " +
                "WHERE info IS NULL AND (charm <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'AGE INVALID' " +
                "WHERE info IS NULL AND date_part('year', age(birth)) NOT BETWEEN 3 AND 1000"); // TODO проверить возраст

        //language=PostgreSQL
        exec("WITH num_ord AS (" +
                "    SELECT num," +
                "    row_number() OVER (PARTITION BY cia_id ORDER BY num DESC ) AS ord " +
                "    FROM TMP_CLIENT" +
                ")" +
                "UPDATE TMP_CLIENT SET info = 'NOT ACTUAL' " +
                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
        exec("UPDATE TMP_PHONE SET info = 'CLIENT INVALID OR NOT ACTUAL'" +
                " FROM TMP_CLIENT " +
                "WHERE TMP_PHONE.info ISNULL AND TMP_CLIENT.info NOTNULL" +
                " AND TMP_CLIENT.num = TMP_PHONE.num");
    }

    private void migrateCharmTable() {
        //language=PostgreSQL
        exec("INSERT INTO charm(name)" +
                "   SELECT DISTINCT charm FROM TMP_CLIENT WHERE TMP_CLIENT.info ISNULL " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateClientTable() {
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET client_id = client.id FROM client " +
                "   WHERE TMP_CLIENT.cia_id = client.cia_client_id AND info ISNULL"); // TODO actual is needed?

        //language=PostgreSQL
        exec("INSERT INTO client (surname, name, patronymic, gender, birth_date, charm, cia_client_id, actual)" +
                "  SELECT " +
                "    surname," +
                "    name," +
                "    patronymic," +
                "    gender," +
                "    birth," +
                "    (SELECT id" +
                "     FROM charm" +
                "     WHERE name LIKE charm)," +
                "    cia_id," +
                "    FALSE" +
                "  FROM TMP_CLIENT" +
                "  WHERE info ISNULL AND client_id ISNULL");

        //language=PostgreSQL
        exec("UPDATE client SET" +
                "  surname = t.surname," +
                "  name = t.name," +
                "  patronymic = t.patronymic," +
                "  gender = t.gender," +
                "  birth_date = t.birth," +
                "  charm = (SELECT id FROM charm WHERE charm.name LIKE t.charm)" +
                "FROM TMP_CLIENT t WHERE client_id NOTNULL AND client.id = client_id");
    }

    private void migrateClientAddressTable() {
        //language=PostgreSQL
        exec("INSERT INTO client_addr(client, type, street, house, flat) " +
                "  SELECT " +
                "    (SELECT id" +
                "     FROM client" +
                "     WHERE cia_client_id = tmp_client.cia_id)," +
                "   type, street, house, flat"+
                "   FROM tmp_client " +
                "   JOIN TMP_ADDRESS ON TMP_CLIENT.num = TMP_ADDRESS.num " +
                "   WHERE TMP_CLIENT.info ISNULL " +
                "   ON CONFLICT(client, type) DO UPDATE " +
                "   SET street = EXCLUDED.street, house = EXCLUDED.house, flat = EXCLUDED.flat");
    }

    private void migrateClientPhoneTable() {
        //language=PostgreSQL
        exec("UPDATE tmp_phone SET client_id = tmp_client.client_id" +
                " FROM tmp_client WHERE tmp_client.num = tmp_phone.num " +
                "AND tmp_phone.info ISNULL AND tmp_client.client_id NOTNULL"); // TODO actual is needed?

        //language=PostgreSQL // TODO not use delete set actual 0
        exec("DELETE FROM client_phone USING tmp_phone " +
                "WHERE tmp_phone.client_id NOTNULL" +
                " AND tmp_phone.info ISNULL " +
                "AND client_phone.client = tmp_phone.client_id");

        //language=PostgreSQL
        exec("INSERT INTO client_phone (client, number, type)" +
                "  SELECT " +
                "    (SELECT id" +
                "     FROM client" +
                "     WHERE cia_client_id = tmp_client.cia_id)," +
                "    phone," +
                "    type" +
                "  FROM tmp_phone" +
                "  JOIN tmp_client ON tmp_client.num = tmp_phone.num" +
                "  WHERE tmp_phone.info ISNULL");
    }

    private void exec(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
