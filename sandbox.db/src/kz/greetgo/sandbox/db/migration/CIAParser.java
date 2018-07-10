package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.controller.model.AddrType;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.util.Insert;
import kz.greetgo.sandbox.db.migration.model.Phone;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CIAParser extends DefaultHandler {

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

    private Client client;
    private Connection connection;
    private int num = 0;
    private int batch = 0;
    private int maxBatchSize;
    private StringBuilder phoneStr;

    CIAParser(Connection connection, int maxBatchSize) {
        this.connection = connection;
        this.maxBatchSize = maxBatchSize;
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
        Insert client = new Insert("TMP_CLIENT");
        client.field(1, "cia_id", "?");
        client.field(2, "name", "?");
        client.field(3, "surname", "?");
        client.field(4, "patronymic", "?");
        client.field(5, "birth", "?");
        client.field(6, "charm", "?");
        client.field(7, "gender", "?");
        client.field(8, "num", "?");

        Insert client_addr = new Insert("TMP_ADDRESS");
        client_addr.field(1, "type", "?");
        client_addr.field(2, "street", "?");
        client_addr.field(3, "house", "?");
        client_addr.field(4, "flat", "?");
        client_addr.field(5, "num", "?");

        Insert client_phone = new Insert("TMP_PHONE");
        client_phone.field(1, "type", "?");
        client_phone.field(2, "phone", "?");
        client_phone.field(3, "num", "?");

        clientPs = connection.prepareStatement(client.toString());
        addrPs = connection.prepareStatement(client_addr.toString());
        phonePs = connection.prepareStatement(client_phone.toString());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case CLIENT:
                num++;
                client = new Client();
                client.cia_id = attributes.getValue("id");
                client.num = num;
                break;
            case NAME:
                client.name = attributes.getValue("value");
                break;
            case SURNAME:
                client.surname = attributes.getValue("value");
                break;
            case PATRONYMIC:
                client.patronymic = attributes.getValue("value");
                break;
            case BIRTH:
                String tmp = attributes.getValue("value");
                client.birth = isValidDate(tmp);
                break;
            case CHARM:
                client.charm = attributes.getValue("value");
                break;
            case GENDER:
                client.gender = attributes.getValue("value");
                break;
            case FACT:
                Address fact = new Address();
                fact.type = AddrType.FACT;
                fact.street = attributes.getValue("street");
                fact.house = attributes.getValue("house");
                fact.flat = attributes.getValue("flat");
                fact.num = num;
                try {
                    insertAddr(fact);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REGISTER:
                Address reg = new Address();
                reg.type = AddrType.REG;
                reg.street = attributes.getValue("street");
                reg.house = attributes.getValue("house");
                reg.flat = attributes.getValue("flat");
                reg.num = num;
                try {
                    insertAddr(reg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private Date isValidDate(String tmp) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            return df.parse(tmp);
        } catch (Exception e) {
            return null;
        }
    }

    private String getPhoneStr() {
        if (phoneStr == null) return "";
        return phoneStr.toString();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        phoneStr = new StringBuilder();
        phoneStr.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            switch (qName) {
                case CLIENT:
                    insertClient(client);
                    break;
                case HOME_PHONE:
                    Phone home = new Phone();
                    home.type = PhoneType.HOME;
                    home.num = num;
                    home.phone = getPhoneStr();
                    insertPhone(home);
                    break;
                case WORK_PHONE:
                    Phone work = new Phone();
                    work.type = PhoneType.WORK;
                    work.num = num;
                    work.phone = getPhoneStr();
                    insertPhone(work);
                    break;
                case MOBILE_PHONE:
                    Phone mob = new Phone();
                    mob.type = PhoneType.MOBILE;
                    mob.num = num;
                    mob.phone = getPhoneStr();
                    insertPhone(mob);
                    break;
            }
        } catch (Exception ignored) {
        }
    }

    private void insertClient(Client client) throws Exception {
        clientPs.setString(1, client.cia_id);
        clientPs.setString(2, client.name);
        clientPs.setString(3, client.surname);
        clientPs.setString(4, client.patronymic);
        if (client.birth != null) // TODO one line
            clientPs.setDate(5, new java.sql.Date(client.birth.getTime()));
        else
            clientPs.setDate(5, null);
        clientPs.setString(6, client.charm);
        clientPs.setString(7, client.gender);
        clientPs.setInt(8, client.num);
        clientPs.addBatch();
        execute();
    }

    private void insertAddr(Address address) throws Exception {
        addrPs.setString(1, address.type.name());
        addrPs.setString(2, address.street);
        addrPs.setString(3, address.house);
        addrPs.setString(4, address.flat);
        addrPs.setInt(5, address.num);
        addrPs.addBatch();
        execute();
    }

    private void insertPhone(Phone phone) throws Exception {
        phonePs.setString(1, phone.type.name());
        phonePs.setString(2, phone.phone);
        phonePs.setInt(3, phone.num);
        phonePs.addBatch();
        execute();
    }

    private void execute() throws Exception {
        batch++;
        if (batch >= maxBatchSize) {
            clientPs.executeBatch();
            phonePs.executeBatch();
            addrPs.executeBatch();
            connection.commit();
            System.out.println("COMMIT " + batch);
            batch = 0;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            if (batch > 0) {
                clientPs.executeBatch();
                phonePs.executeBatch();
                addrPs.executeBatch();
                connection.commit();
                System.out.println("COMMIT " + batch);
            }
            clientPs.close();
            addrPs.close();
            phonePs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.endDocument();
    }

}
