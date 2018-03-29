package kz.greetgo.sandbox.db.migration_impl;

import kz.greetgo.sandbox.db.migration_impl.model.Address;
import kz.greetgo.sandbox.db.migration_impl.model.ClientTmp;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneNumber;
import kz.greetgo.sandbox.db.migration_impl.model.PhoneType;
import kz.greetgo.sandbox.db.util.SaxHandler;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CiaParser extends SaxHandler {

  private ClientTmp client;
  private InputStream inputStream;
  private CiaTableWorker ciaTableWorker;

  private int recordsNum;
  public OutputStream outError;

  public CiaParser(InputStream inputStream, CiaTableWorker ciaTableWorker, int recordsNum) throws SQLException {
    this.inputStream = inputStream;
    this.ciaTableWorker = ciaTableWorker;
    this.recordsNum = recordsNum;
  }

  public int parseAndSave() throws SAXException, IOException, SQLException {
    if (inputStream == null) return 0;

    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    try {
      reader.parse(new InputSource(inputStream));
    } catch (SAXParseException e) {
      outError.write(e.toString().getBytes());
    }
    return recordsNum;
  }

  @Override
  protected void startingTag(Attributes attributes) throws Exception {
    String path = path();

    switch (path) {
      case "/cia/client":
        client = new ClientTmp();
        client.cia_id = attributes.getValue("id");
        client.number = ++recordsNum;
        return;

      case "/cia/client/surname":
        client.surname = attributes.getValue("value");
        return;

      case "/cia/client/name":
        client.name = attributes.getValue("value");
        return;

      case "/cia/client/patronymic":
        client.patronymic = attributes.getValue("value");
        return;

      case "/cia/client/gender":
        client.gender = attributes.getValue("value");
        return;

      case "/cia/client/birth":
        try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          client.birth_date = sdf.parse(attributes.getValue("value"));
        } catch (ParseException e) {
          // Проглатывание ошибки
          return;
        }
        return;

      case "/cia/client/charm":
        client.charm_name = attributes.getValue("value");
        return;

      case "/cia/client/address/fact":
        Address addressFact = new Address("FACT");
        addressFact.street = attributes.getValue("street");
        addressFact.house = attributes.getValue("house");
        addressFact.flat = attributes.getValue("flat");
        addressFact.cia_id = client.cia_id;
        addressFact.client_num = recordsNum;
        ciaTableWorker.addToBatch(addressFact);
        return;

      case "/cia/client/address/register":
        Address addressReg = new Address("REG");
        addressReg.street = attributes.getValue("street");
        addressReg.house = attributes.getValue("house");
        addressReg.flat = attributes.getValue("flat");
        addressReg.cia_id = client.cia_id;
        addressReg.client_num = recordsNum;
        ciaTableWorker.addToBatch(addressReg);
    }
  }

  @Override
  protected void endedTag(String tagName) throws Exception {
    String path = path() + "/" + tagName;

    if (path.endsWith("Phone")) {
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.client_num = recordsNum;
      phoneNumber.phone_number = text();
      switch (path) {
        case "/cia/client/workPhone": {
          phoneNumber.type = PhoneType.WORK;
          break;
        }

        case "/cia/client/mobilePhone": {
          phoneNumber.type = PhoneType.MOBILE;
          break;
        }

        case "/cia/client/homePhone": {
          phoneNumber.type = PhoneType.HOME;
          break;
        }

        default:
          throw new RuntimeException("Unexpected phone type");
      }
      ciaTableWorker.phonesQueue.offer(phoneNumber);
    } else if ("/cia/client".equals(path)) {
      ciaTableWorker.addToBatch(client);
    }
  }
}
