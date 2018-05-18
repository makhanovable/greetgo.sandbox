package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;
import kz.greetgo.sandbox.db.migration.util.SaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;

public class FromXMLParser extends SaxHandler {
    ClientXMLRecord clientXMLRecord;

    public ClientXMLRecord parseRecordData(Long number, String recordData) throws SAXException, IOException {
        if (recordData == null) return null;

        clientXMLRecord = new ClientXMLRecord();
        clientXMLRecord.number = number;

        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(recordData)));

        return clientXMLRecord;
    }

    @Override
    protected void startingTag(Attributes attributes) throws Exception {
        String path = path();
        if ("/client".equals(path)) {
            clientXMLRecord.id = attributes.getValue("id");
            return;
        }
        if ("/client/name".equals(path)) {
            clientXMLRecord.name = attributes.getValue("value");
            return;
        }
        if ("/client/surname".equals(path)) {
            clientXMLRecord.surname = attributes.getValue("value");
            return;
        }
        if ("/client/patronymic".equals(path)) {
            clientXMLRecord.patronymic = attributes.getValue("value");
            return;
        }
        if ("/client/gender".equals(path)) {
            clientXMLRecord.gender = attributes.getValue("value");
            return;
        }
        if ("/client/charm".equals(path)) {
            clientXMLRecord.charm = attributes.getValue("value");
            return;
        }
        if ("/client/birth".equals(path)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            clientXMLRecord.birthDate = new java.sql.Date(sdf.parse(attributes.getValue("value")).getTime());
            return;
        }

        if ("/client/address/fact".equals(path)) {
            clientXMLRecord.fStreet = attributes.getValue("street");
            clientXMLRecord.fHouse = attributes.getValue("house");
            clientXMLRecord.fFlat = attributes.getValue("flat");
            return;
        }
        if ("/client/address/register".equals(path)) {
            clientXMLRecord.rStreet = attributes.getValue("street");
            clientXMLRecord.rHouse = attributes.getValue("house");
            clientXMLRecord.rFlat = attributes.getValue("flat");
            return;
        }
    }

    @Override
    protected void endedTag(String tagName) throws Exception {
        String path = path() + "/" + tagName;

        if ("/client/homePhone".equals(path)) {
            clientXMLRecord.homePhones.add(text());
            return;
        }
        if ("/client/workPhone".equals(path)) {
            clientXMLRecord.workPhones.add(text());
            return;
        }
        if ("/client/mobilePhone".equals(path)) {
            clientXMLRecord.mobilePhones.add(text());
            return;
        }
    }
}
