package kz.greetgo.sandbox.db.migration;

import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;

public class CIAMigration extends MigrationAbstract {

    private String path;
    private int maxBatchSize;
    private Connection connection;

    public CIAMigration(Connection connection, String path, int maxBatchSize) {
        super(connection);
        this.connection = connection;
        this.maxBatchSize = maxBatchSize;
        this.path = path;
    }

    @Override
    public void migrate() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Starting parsing and insert " + path);

        {
            createTempTables();
            prepareData();
        }

        System.out.println("Time to parsing and inserting " + (System.currentTimeMillis() - start) + " " + path);
        start = System.currentTimeMillis();
        System.out.println("Starting inner migration " + path);

        {
            validateTableData();
            migrateCharmTable();
            migrateClientTable();
            migrateClientAddressTable();
            migrateClientPhoneTable();
            finishMigration();
        }

        System.out.println("Time to inner migration " + (System.currentTimeMillis() - start) + " " + path);
    }

    private void prepareData() throws Exception {
        connection.setAutoCommit(false);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        CIAParser parser = new CIAParser(connection, maxBatchSize);
        xmlReader.setContentHandler(parser);
        xmlReader.parse(path);
        connection.setAutoCommit(true);
    }

    private void createTempTables() {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_CLIENT CASCADE;" +
                "CREATE TABLE TMP_CLIENT (" +
                "   info VARCHAR(50)," +
                "   cia_id VARCHAR(50)," +
                "   client_id BIGINT DEFAULT NULL," +
                "   name VARCHAR(50)," +
                "   surname VARCHAR(50)," +
                "   patronymic VARCHAR(50)," +
                "   birth DATE," +
                "   charm VARCHAR(50)," +
                "   gender VARCHAR(50)," +
                "   num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_ADDRESS CASCADE;" +
                "CREATE TABLE TMP_ADDRESS (" +
                "   type VARCHAR(10)," +
                "   street VARCHAR(50)," +
                "   house VARCHAR(10)," +
                "   flat VARCHAR(10)," +
                "   num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS TMP_PHONE CASCADE;" +
                "CREATE TABLE TMP_PHONE (" +
                "   type VARCHAR(10)," +
                "   phone VARCHAR(50)," +
                "   num BIGINT)");
    }

    private void validateTableData() {

        long s = System.currentTimeMillis();
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'SURNAME INVALID'" +
                " WHERE info ISNULL AND (surname <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'NAME INVALID'" +
                " WHERE info ISNULL AND (name <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'BIRTH_DATE INVALID'" +
                " WHERE info ISNULL AND (birth ISNULL OR (date_part('year', age(birth)) NOT BETWEEN 3 AND 1000))");
        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET info = 'CHARM INVALID'" +
                "WHERE info IS NULL AND (charm <> '') IS NOT TRUE");

        //language=PostgreSQL
        exec("WITH num_ord AS (" +
                "    SELECT num," +
                "    row_number() OVER (PARTITION BY cia_id ORDER BY num DESC ) AS ord " +
                "    FROM TMP_CLIENT" +
                ")" +
                "UPDATE TMP_CLIENT SET info = 'NOT ACTUAL' " +
                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT SET client_id = client.id FROM client " +
                "WHERE TMP_CLIENT.cia_id = client.cia_client_id AND info ISNULL");

        //language=PostgreSQL // TODO not use delete set actual 0
        exec("DELETE FROM client_phone USING TMP_CLIENT " +
                "WHERE TMP_CLIENT.client_id NOTNULL" +
                " AND TMP_CLIENT.info ISNULL " +
                "AND client_phone.client = TMP_CLIENT.client_id");
    }

    private void migrateCharmTable() {
        //language=PostgreSQL
        exec("INSERT INTO charm(name)" +
                "   SELECT DISTINCT charm FROM TMP_CLIENT WHERE TMP_CLIENT.info ISNULL " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateClientTable() {
        //language=PostgreSQL
        exec("UPDATE client SET" +
                "  surname = t.surname," +
                "  name = t.name," +
                "  patronymic = t.patronymic," +
                "  gender = t.gender," +
                "  birth_date = t.birth," +
                "  charm = c.id " +
                "FROM TMP_CLIENT t " +
                "JOIN charm c ON c.name LIKE t.charm " +
                "WHERE t.client_id NOTNULL AND client.id = t.client_id AND t.info ISNULL");

        //language=PostgreSQL
        exec("INSERT INTO client (surname, name, patronymic, gender, birth_date, charm, cia_client_id, actual)" +
                "  SELECT " +
                "    t.surname," +
                "    t.name," +
                "    t.patronymic," +
                "    t.gender," +
                "    t.birth," +
                "    c.id," +
                "    t.cia_id," +
                "    FALSE " +
                "FROM TMP_CLIENT t " +
                "JOIN charm c ON c.name LIKE t.charm " +
                "WHERE t.client_id ISNULL AND t.info ISNULL");

        //language=PostgreSQL
        exec("UPDATE TMP_CLIENT tc SET " +
                "client_id = c.id " +
                "FROM client c " +
                "WHERE tc.cia_id = c.cia_client_id " +
                "AND tc.info ISNULL " +
                "AND tc.client_id ISNULL");
    }

    private void migrateClientAddressTable() {
        //language=PostgreSQL
        exec("INSERT INTO client_addr(client, type, street, house, flat) " +
                "  SELECT " +
                "    t.client_id," +
                "   adr.type, adr.street, adr.house, adr.flat" +
                "   FROM TMP_ADDRESS adr" +
                "   JOIN TMP_CLIENT t ON t.num = adr.num " +
                "   WHERE t.info ISNULL " +
                "   ON CONFLICT(client, type) DO UPDATE " +
                "   SET street = EXCLUDED.street, house = EXCLUDED.house, flat = EXCLUDED.flat");
    }

    private void migrateClientPhoneTable() {
        //language=PostgreSQL
        exec("INSERT INTO client_phone (client, number, type)" +
                "  SELECT " +
                "    t.client_id," +
                "    ph.phone," +
                "    ph.type" +
                "  FROM tmp_phone ph" +
                "  JOIN tmp_client t ON t.num = ph.num" +
                "  WHERE t.info ISNULL");
    }

    private void finishMigration() {
        //language=PostgreSQL
        exec("UPDATE client SET actual = TRUE FROM" +
                " tmp_client WHERE tmp_client.client_id = client.id" +
                " AND tmp_client.info ISNULL");
    }

}
