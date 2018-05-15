package kz.greetgo.sandbox.controller.migration.core;

import kz.greetgo.sandbox.controller.migration.util.SaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientRecord extends SaxHandler {
    public long number;
    public String id;
    public String surname, name, patronymic;
    public String charm, gender;
    public java.sql.Date birthDate;
    public String fStreet, fHouse, fFlat;
    public String rStreet, rHouse, rFlat;
    public List<String> homePhones = new ArrayList<>();
    public List<String> workPhones = new ArrayList<>();
    public List<String> mobilePhones = new ArrayList<>();

    public void parseRecordData(String recordData) throws SAXException, IOException {
        if (recordData == null) return;
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(recordData)));
    }

    @Override
    protected void startingTag(Attributes attributes) throws Exception {
        String path = path();
        if ("/client".equals(path)) {
            id = attributes.getValue("id");
            return;
        }
        if ("/client/name".equals(path)) {
            name = attributes.getValue("value");
            return;
        }
        if ("/client/surname".equals(path)) {
            surname = attributes.getValue("value");
            return;
        }
        if ("/client/patronymic".equals(path)) {
            patronymic = attributes.getValue("value");
            return;
        }
        if ("/client/gender".equals(path)) {
            gender = attributes.getValue("value");
            return;
        }
        if ("/client/charm".equals(path)) {
            charm = attributes.getValue("value");
            return;
        }
        if ("/client/birth".equals(path)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            birthDate = new java.sql.Date(sdf.parse(attributes.getValue("value")).getTime());
            return;
        }

        if ("client/address/fact".equals(path)) {
            fStreet = attributes.getValue("street");
            fHouse = attributes.getValue("house");
            fFlat = attributes.getValue("flat");
            return;
        }
        if ("client/address/register".equals(path)) {
            rStreet = attributes.getValue("street");
            rHouse = attributes.getValue("house");
            rFlat = attributes.getValue("flat");
            return;
        }
    }

    @Override
    protected void endedTag(String tagName) throws Exception {
        String path = path() + "/" + tagName;

        if ("/client/homePhone".equals(path)) {
            homePhones.add(text());
            return;
        }
        if ("/client/workPhone".equals(path)) {
            workPhones.add(text());
            return;
        }
        if ("/client/mobilePhone".equals(path)) {
            mobilePhones.add(text());
            return;
        }
    }
}
