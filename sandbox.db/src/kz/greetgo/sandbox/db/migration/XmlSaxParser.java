package kz.greetgo.sandbox.db.migration;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XmlSaxParser extends DefaultHandler {

    private final static String CLIENT = "client";
    private final static String NAME = "name";
    private final static String SURNAME = "surname";
    private final static String PATRONYMIC = "patronymic";
    private final static String BIRTH = "birth";
    private final static String CHARM = "charm";
    private final static String GENDER = "gender";
    private final static String FACT = "fact";
    private final static String REGISTER = "register";
    private final static String WORK_PHONE = "workPhone";
    private final static String HOME_PHONE = "homePhone";
    private final static String MOBILE_PHONE = "mobilePhone";

    private Migration migration;
    private ClientRecord clientRecord;
    private List<Addr> addrs;
    private Addr addr;
    private Phone phone;
    private List<Phone> phones;
    private String currentTag;
    public int count = 0;

    XmlSaxParser(Migration migration) {
        this.migration = migration;
    }


    private PreparedStatement clientPs;
    private PreparedStatement addrPs;
    private PreparedStatement phonePs;

    @Override
    public void startDocument() throws SAXException {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.startDocument();
    }

    private void start() throws Exception {
        Insert client = new Insert("TMP_CIA");
        client.field(1, "id", "?");
        client.field(2, "name", "?");
        client.field(3, "surname", "?");
        client.field(4, "patronymic", "?");
        client.field(5, "birth", "?");
        client.field(6, "charm", "?");
        client.field(7, "gender", "?");

        Insert client_addr = new Insert("TMP_ADDRESS");
        client_addr.field(1, "client", "?");
        client_addr.field(2, "type", "?");
        client_addr.field(3, "street", "?");
        client_addr.field(4, "house", "?");
        client_addr.field(5, "flat", "?");

        Insert client_phone = new Insert("TMP_PHONE");
        client_phone.field(1, "client", "?");
        client_phone.field(2, "type", "?");
        client_phone.field(3, "number", "?");

        migration.connection.setAutoCommit(false);
        clientPs = migration.connection.prepareStatement(client.toString());
        addrPs = migration.connection.prepareStatement(client_addr.toString());
        phonePs = migration.connection.prepareStatement(client_phone.toString());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName;
        String tmp;
        switch (currentTag) {
            case CLIENT:
                count++;
                clientRecord = new ClientRecord();
                clientRecord.id = attributes.getValue("id");
                phones = new ArrayList<>();
                phone = new Phone();
                addrs = new ArrayList<>();
                break;
            case NAME:
                tmp = attributes.getValue("value");
                if (tmp != null && tmp.replace(" ", "").isEmpty())
                    clientRecord.name = null;
                else clientRecord.name = attributes.getValue("value");
                break;
            case SURNAME:
                tmp = attributes.getValue("value");
                if (tmp != null && tmp.replace(" ", "").isEmpty())
                    clientRecord.surname = null;
                else clientRecord.surname = attributes.getValue("value");
                break;
            case PATRONYMIC:
                tmp = attributes.getValue("value");
                if (tmp != null && tmp.replace(" ", "").isEmpty())
                    clientRecord.patronymic = null;
                else clientRecord.patronymic = attributes.getValue("value");
                break;
            case BIRTH:
                clientRecord.birth = attributes.getValue("value");
                break;
            case CHARM:
                clientRecord.charm = attributes.getValue("value");
                break;
            case GENDER:
                clientRecord.gender = attributes.getValue("value");
                break;
            case FACT:
                addr = new Addr();
                addr.type = FACT;
                addr.street = attributes.getValue("street");
                addr.house = attributes.getValue("house");
                addr.flat = attributes.getValue("flat");
                addrs.add(addr);
                clientRecord.addrs = addrs;
                break;
            case REGISTER:
                addr = new Addr();
                addr.type = REGISTER;
                addr.street = attributes.getValue("street");
                addr.house = attributes.getValue("house");
                addr.flat = attributes.getValue("flat");
                addrs.add(addr);
                clientRecord.addrs = addrs;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case CLIENT:
                try {
                    insertToTmpTable(clientRecord);
                } catch (Exception ignored) {
                }
                clientRecord = null;
                phones = null;
                phone = null;
                addrs = null;
                addr = null;
                break;
        }
        currentTag = null;
    }

    @Override
    public void characters(char[] ch, int start, int end) throws SAXException {
        String str = new String(ch, start, end); // TODO not works correctly
        if (currentTag != null)
            switch (currentTag) {
                case HOME_PHONE:
                    phone.type = HOME_PHONE;
                    phone.number = str;
                    phones.add(phone);
                    clientRecord.phones = phones;
                    break;
                case WORK_PHONE:
                    phone.type = WORK_PHONE;
                    phone.number = str;
                    phones.add(phone);
                    clientRecord.phones = phones;
                    break;
                case MOBILE_PHONE:
                    phone.type = MOBILE_PHONE;
                    phone.number = str;
                    phones.add(phone);
                    clientRecord.phones = phones;
                    break;
            }
    }

    int batchSize = 0, downloadMaxBatchSize = 150;
    private void insertToTmpTable(ClientRecord c) throws Exception {
        clientPs.setString(1, c.id);
        clientPs.setString(2, c.name);
        clientPs.setString(3, c.surname);
        clientPs.setString(4, c.patronymic);
        clientPs.setString(5, c.birth);
        clientPs.setString(6, c.charm);
        clientPs.setString(7, c.gender);
        clientPs.addBatch();

        for (int i = 0; i < c.addrs.size(); i++) {
            addrPs.setString(1, c.id);
            addrPs.setString(2, c.addrs.get(i).type);
            addrPs.setString(3, c.addrs.get(i).street);
            addrPs.setString(4, c.addrs.get(i).house);
            addrPs.setString(5, c.addrs.get(i).flat);
            addrPs.addBatch();
        }

        for (int i = 0; i < c.phones.size(); i++) {
            phonePs.setString(1, c.id);
            phonePs.setString(2, c.phones.get(i).type);
            phonePs.setString(3, c.phones.get(i).number);
            phonePs.addBatch();
        }
        batchSize++;
        if (batchSize >= downloadMaxBatchSize) {
            System.out.println("COMMIT " + batchSize);
            clientPs.executeBatch();
            phonePs.executeBatch();
            addrPs.executeBatch();
            migration.connection.commit();
            batchSize = 0;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            if (batchSize >= 0) {
                clientPs.executeBatch();
                phonePs.executeBatch();
                addrPs.executeBatch();
                migration.connection.commit();
            }
            clientPs.close();
            addrPs.close();
            phonePs.close();
            migration.connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.endDocument();
    }
}
