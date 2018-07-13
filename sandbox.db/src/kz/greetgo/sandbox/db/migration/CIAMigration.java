package kz.greetgo.sandbox.db.migration;

import org.apache.log4j.Logger;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;

public class CIAMigration extends MigrationAbstract {

    private String path;
    private int maxBatchSize;
    private Connection connection;
    private final Logger logger = Logger.getLogger(CIAMigration.class);

    public CIAMigration(Connection connection, String path, int maxBatchSize) {
        super(connection);
        this.connection = connection;
        this.maxBatchSize = maxBatchSize;
        this.path = path;
        logger.info("Starting CIA Migration of file: " + path);
    }

    @Override
    public void migrate() throws Exception {
        logger.info("CIAMigration.createTempTables()");
        createTempTables();

        logger.info("CIAMigration.prepareData()");
        prepareData();

        logger.info("CIAMigration.validateTableData()");
        validateTableData();

        logger.info("CIAMigration.migrateCharmTable()");
        migrateCharmTable();

        logger.info("CIAMigration.migrateClientTable()");
        migrateClientTable();

        logger.info("CIAMigration.migrateClientAddressTable()");
        migrateClientAddressTable();

        logger.info("CIAMigration.migrateClientPhoneTable()");
        migrateClientPhoneTable();

        logger.info("CIAMigration.finishMigration()");
        finishMigration();

        logger.info("CIAMigration.loadTopSqlQueriesList()");
        loadTopSqlQueriesList();
    }

    private void prepareData() throws Exception {
        long start = System.currentTimeMillis();
        logger.info("Starting parseAndInsertDataToTempTables file: " + path);

        connection.setAutoCommit(false);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        CIAParser parser = new CIAParser(connection, maxBatchSize);
        xmlReader.setContentHandler(parser);
        xmlReader.parse(path);
        connection.setAutoCommit(true);

        long end = System.currentTimeMillis();
        logger.info("Time to parseAndInsertDataToTempTables: " + (end - start) + " ms");
    }

    private void createTempTables() throws Exception {
        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS tmp_client CASCADE; " +
                "CREATE TABLE tmp_client (" +
                "error VARCHAR(50)," +
                " status INTEGER DEFAULT 0," +
                " cia_id VARCHAR(50)," +
                " client_id BIGINT," +
                " name VARCHAR(50)," +
                " surname VARCHAR(50)," +
                " patronymic VARCHAR(50)," +
                " birth DATE," +
                " charm VARCHAR(50)," +
                " gender VARCHAR(50)," +
                " num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS tmp_address CASCADE; " +
                "CREATE TABLE tmp_address (" +
                "type VARCHAR(10)," +
                " street VARCHAR(50)," +
                " house VARCHAR(10)," +
                " flat VARCHAR(10)," +
                " num BIGINT)");

        //language=PostgreSQL
        exec("DROP TABLE IF EXISTS tmp_phone CASCADE; " +
                "CREATE TABLE tmp_phone (" +
                " type VARCHAR(10)," +
                " phone VARCHAR(50)," +
                " num BIGINT)");
    }

    private void validateTableData() throws Exception {
        //language=PostgreSQL
        exec("UPDATE tmp_client SET error = 'SURNAME INVALID', status = 3" +
                " WHERE status = 0 AND (surname <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE tmp_client SET error = 'NAME INVALID', status = 3" +
                " WHERE status = 0 AND (name <> '') IS NOT TRUE");
        //language=PostgreSQL
        exec("UPDATE tmp_client SET error = 'BIRTH_DATE INVALID', status = 3" +
                " WHERE status = 0 AND (birth ISNULL OR (date_part('year', age(birth)) NOT BETWEEN 3 AND 1000))");
        //language=PostgreSQL
        exec("UPDATE tmp_client SET error = 'CHARM INVALID', status = 3" +
                " WHERE status = 0 AND (charm <> '') IS NOT TRUE");

        //language=PostgreSQL
        exec("WITH num_ord AS (" +
                "SELECT num," +
                " row_number() OVER (PARTITION BY cia_id ORDER BY num DESC ) AS ord" +
                " FROM tmp_client" +
                ") " +
                "UPDATE tmp_client SET status = 2 " +
                "WHERE num IN (SELECT num FROM num_ord WHERE ord > 1)");

        //language=PostgreSQL
        exec("UPDATE tmp_client SET client_id = client.id FROM client " +
                "WHERE tmp_client.cia_id = client.cia_client_id AND status = 0");

        //language=PostgreSQL
        exec("DELETE FROM client_phone USING tmp_client " +
                "WHERE tmp_client.client_id NOTNULL " +
                "AND tmp_client.status = 0 " +
                "AND client_phone.client = tmp_client.client_id");
    }

    private void migrateCharmTable() throws Exception {
        //language=PostgreSQL
        exec("INSERT INTO charm(name) " +
                "SELECT DISTINCT charm FROM tmp_client WHERE tmp_client.status = 0 " +
                "ON CONFLICT (name) DO NOTHING");
    }

    private void migrateClientTable() throws Exception {
        //language=PostgreSQL
        exec("UPDATE client SET " +
                "surname = t.surname," +
                " name = t.name," +
                " patronymic = t.patronymic," +
                " gender = t.gender," +
                " birth_date = t.birth," +
                " charm = c.id " +
                "FROM TMP_CLIENT t " +
                "JOIN charm c ON c.name LIKE t.charm " +
                "WHERE t.client_id NOTNULL AND client.id = t.client_id AND t.status = 0");

        //language=PostgreSQL
        exec("INSERT INTO client (surname, name, patronymic, gender, birth_date, charm, cia_client_id, actual)" +
                " SELECT" +
                " t.surname," +
                " t.name," +
                " t.patronymic," +
                " t.gender," +
                " t.birth," +
                " c.id," +
                " t.cia_id," +
                " FALSE " +
                "FROM TMP_CLIENT t " +
                "JOIN charm c ON c.name LIKE t.charm " +
                "WHERE t.client_id ISNULL AND t.status = 0");

        //language=PostgreSQL
        exec("UPDATE tmp_client SET status = 4 WHERE status = 0" +
                " AND client_id NOTNULL"); // 4 проапдейтился

        //language=PostgreSQL
        exec("UPDATE tmp_client SET status = 5 WHERE status = 0" +
                " AND client_id ISNULL"); // 5 проинсертился

        //language=PostgreSQL
        exec("UPDATE tmp_client tc SET " +
                "client_id = c.id " +
                "FROM client c " +
                "WHERE tc.cia_id = c.cia_client_id " +
                "AND tc.status IN (4, 5) " +
                "AND tc.client_id ISNULL");
    }

    private void migrateClientAddressTable() throws Exception {
        //language=PostgreSQL
        exec("INSERT INTO client_addr(client, type, street, house, flat) " +
                "SELECT" +
                " t.client_id," +
                " adr.type, adr.street, adr.house, adr.flat " +
                "FROM tmp_address adr " +
                "JOIN tmp_client t ON t.num = adr.num " +
                "WHERE t.status IN (4, 5) " +
                "ON CONFLICT(client, type) DO UPDATE " +
                "SET street = EXCLUDED.street, house = EXCLUDED.house, flat = EXCLUDED.flat");
    }

    private void migrateClientPhoneTable() throws Exception {
        //language=PostgreSQL
        exec("INSERT INTO client_phone (client, number, type) " +
                "SELECT" +
                " t.client_id," +
                " ph.phone," +
                " ph.type " +
                "FROM tmp_phone ph " +
                "JOIN tmp_client t ON t.num = ph.num " +
                "WHERE t.status IN (4, 5)");
    }

    private void finishMigration() throws Exception {
        //language=PostgreSQL
        exec("UPDATE client SET actual = TRUE FROM" +
                " tmp_client WHERE tmp_client.client_id = client.id " +
                "AND tmp_client.status IN (4, 5)");
    }

}
