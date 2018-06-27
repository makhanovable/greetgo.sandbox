package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.Vector;

public class Migration {

    public Connection connection;

    Migration(Connection connection) {
        this.connection = connection;
    }

    public void migrate() throws Exception {

        //
        // ПОДГОТОВКА МИГРАЦИИ
        //
        createTempTables();
        prepareData();
        //
        // МИГРАЦИЯ
        //
        migrateFromTmp();


    }

    private void prepareData() throws Exception {
        downloadFilesAndUnZip();
    }

    @SuppressWarnings("unchecked")
    private void downloadFilesAndUnZip() throws Exception {
//        String[] FOLDERS = new String[]{"/var/metodology/100_000/", "/var/metodology/1_000_000/"};
//        String[] FOLDERS = new String[]{"/var/metodology/1_000_000/"};
        String[] FOLDERS = new String[]{"/var/metodology/100_000/"};
        String USER = "makhan";
        String PASSWORD = "arduino121232";
        String HOST = "localhost";
        int PORT = 2220;

        Session session;
        Channel channel;
        ChannelSftp channelSftp;

        JSch jsch = new JSch();
        session = jsch.getSession(USER, HOST, PORT);
        session.setPassword(PASSWORD);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;

        for (String FOLDER : FOLDERS) {
            channelSftp.cd(FOLDER);
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(FOLDER);
            for (ChannelSftp.LsEntry oListItem : files) {
                if (!oListItem.getAttrs().isDir()) {
                    String dest = "build" + FOLDER + oListItem.getFilename();
                    channelSftp.get(oListItem.getFilename(), dest);
//                    if (dest.endsWith("tar.bz2")) { // TODO extract and delete
//                        String extractedFilePath = extract(dest);
//                        if (extractedFilePath.endsWith("xml"))
//                            parseXmlAndInsertToTempTable(extractedFilePath);
//                        else
//                            parseJsonAndInsertToTempTable(extractedFilePath);
//                    }
                    if (dest.endsWith("from_cia_2018-02-21-154532-1-300.xml.tar.bz2")) {
                        String extractedFilePath = extract(dest);
                        parseXmlAndInsertToTempTable(extractedFilePath);
                    } else if (dest.endsWith("from_frs_2018-02-21-154543-1-30009.json_row.txt.tar.bz2")) {
                        String extractedFilePath = extract(dest);
                        parseJsonAndInsertToTempTable(extractedFilePath);
                    }
//                    if (dest.endsWith("from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2")) {
//                        String extractedFilePath = extract(dest);
//                        parseXmlAndInsertToTempTable(extractedFilePath);
//                    } else if (dest.endsWith("from_frs_2018-02-21-155112-1-30002.json_row.txt.tar.bz2")) {
//                        String extractedFilePath = extract(dest);
//                        parseJsonAndInsertToTempTable(extractedFilePath);
//                    }
                }
            }
        }

        channelSftp.exit();
        session.disconnect();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String extract(String tarFileName) throws Exception {
        System.out.println();
        long start = System.currentTimeMillis();
        System.out.println("starting extracting ....." + tarFileName);
        String path = null;
        File dest = new File("build/extracted/");
        TarInputStream tin;
        if (!dest.exists())
            dest.mkdir();
        tin = new TarInputStream(getDecompressedInputStream(new FileInputStream
                (new File(tarFileName))));
        TarEntry tarEntry = tin.getNextEntry();

        while (tarEntry != null) {
            File destPath = new File(dest.toString() + File.separatorChar + tarEntry.getName());
            path = destPath.getPath();
            if (tarEntry.isDirectory())
                destPath.mkdir();
            else {
                if (!destPath.getParentFile().exists())
                    destPath.getParentFile().mkdirs();
                FileOutputStream fout = new FileOutputStream(destPath);
                tin.copyEntryContents(fout);
                fout.flush();
                fout.close();
            }
            tarEntry = tin.getNextEntry();
        }
        tin.close();
        long end = System.currentTimeMillis();
        System.out.println("EXTRACTED TIME = " + (end - start));
        return path;
    }

    private static InputStream getDecompressedInputStream(final InputStream stream) throws Exception {
        final char[] magic = new char[]{'B', 'Z'};
        for (char aMagic : magic) {
            if (stream.read() != aMagic) {
                throw new RuntimeException("Invalid bz2 file");
            }
        }
        return new BufferedInputStream(new CBZip2InputStream(stream));
    }

    private void createTempTables() { // TODO create norm tables
        exec("DROP TABLE IF EXISTS TMP_CIA CASCADE;" +
                "CREATE TABLE TMP_CIA (status varchar(255) default 'JUST_INSERTED', id varchar(255), name varchar(255), surname varchar(255), patronymic varchar(255), birth varchar(255), charm varchar(255), gender varchar(255))");

        exec("DROP TABLE IF EXISTS TMP_ADDRESS CASCADE;" +
                "CREATE TABLE TMP_ADDRESS (status varchar(255) default 'JUST_INSERTED', client varchar(255), type varchar(50), street varchar(255), house varchar(50), flat varchar(50))");

        exec("DROP TABLE IF EXISTS TMP_PHONE CASCADE;" +
                "CREATE TABLE TMP_PHONE (status varchar(255) default 'JUST_INSERTED', client varchar(255), type varchar(50), number varchar(50))");

        exec("DROP TABLE IF EXISTS TMP_FRS CASCADE;" +
                "CREATE TABLE TMP_FRS (status varchar(255) default 'JUST_INSERTED', type varchar(255), money varchar(255), finished_at varchar(255), transaction_type varchar(255), account_number varchar(255), client_id varchar(255), registered_at varchar(255))");
    }

    private void exec(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void parseXmlAndInsertToTempTable(String extractedXmlPath) throws Exception {
        System.out.println("starting parsing XML and inserting to tmp ......");
        long start = System.currentTimeMillis();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        XmlSaxParser parser = new XmlSaxParser(this);

        xmlReader.setContentHandler(parser);
        xmlReader.parse(extractedXmlPath);
        System.out.println("ending parsing XML and insering to tmp....");
        long end = System.currentTimeMillis();
        System.out.println("TIME TO PARSE and INSERT = " + (end - start));
        System.out.println("INSERTED = " + parser.count + " XML");
    }

    private void parseJsonAndInsertToTempTable(String extractedJsonPath) throws Exception {
        System.out.println("starting parsing JSON and inserting to tmp ......");
        long start = System.currentTimeMillis();
        BufferedReader bf = new BufferedReader(new FileReader(extractedJsonPath));
        String line;
        ObjectMapper mapper = new ObjectMapper();
        int a = 0;

        Insert acc = new Insert("TMP_FRS");
        acc.field(1, "type", "?");
        acc.field(2, "money", "?");
        acc.field(3, "finished_at", "?");
        acc.field(4, "transaction_type", "?");
        acc.field(5, "account_number", "?");
        acc.field(6, "client_id", "?");
        acc.field(7, "registered_at", "?");

        connection.setAutoCommit(false);
        try (PreparedStatement ps = connection.prepareStatement(acc.toString())) {
            int batchSize = 0, downloadMaxBatchSize = 10_000;
            while ((line = bf.readLine()) != null) {
                Account account = mapper.readValue(line, Account.class);
                a++;

                ps.setString(1, account.type);
                ps.setString(2, account.money);
                ps.setString(3, account.finished_at);
                ps.setString(4, account.transaction_type);
                ps.setString(5, account.account_number);
                ps.setString(6, account.client_id);
                ps.setString(7, account.registered_at);
                ps.addBatch();
                batchSize++;
                if (batchSize >= downloadMaxBatchSize) {
                    System.out.println("COMMIT " + batchSize);
                    ps.executeBatch();
                    connection.commit();
                    batchSize = 0;
                }
            }
            if (batchSize > 0) {
                ps.executeBatch();
                connection.commit();
            }
        }
        connection.setAutoCommit(true);
        System.out.println("ending parsing JSON and insering to tmp....");
        long end = System.currentTimeMillis();
        System.out.println("TIME TO PARSE and INSERT = " + (end - start));
        System.out.println("INSERTED = " + a + " JSON");
    }

    private void migrateFromTmp() {
        System.out.println("STARTING MIGRATION ...");
        long start = System.currentTimeMillis();

        // TODO set errors

        exec("insert into client(surname, name, patronymic, gender, birth_date, charm) select"
        +" surname, name, patronymic, gender, '2015-01-01', 4 from TMP_CIA where surname NOTNULL AND name NOTNULL");


        long end = System.currentTimeMillis();
        System.out.println("TIME TO MIGRATION = " + (end - start));

    }

}
