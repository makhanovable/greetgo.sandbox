package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.db.stand.model.*;


import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<String, ClientDot> clientStorage = new HashMap<>();
  public final Map<String, AdressDot> adressStorage = new HashMap<>();
  public final Map<String, CharmDot> charmStorage = new HashMap<>();
  public final Map<String, TransactionDot> transactionStorage = new HashMap<>();
  public final Map<String, TransactionType> transactionTypeStorage = new HashMap<>();
  public final Map<String, PhoneDot> phoneStorage = new HashMap<>();
  public final Map<String, AccountDot> accountStorage = new HashMap<>();

  private int clientsNum;
  private String clientID;

  @Override
  public void afterInject() throws Exception {
    appendPerson(getData("StandDbInitData.txt"));
    appendClient(getData("ClientDb.txt"));
    appendCharm(getData("CharmDb.txt"));
    appendAdress(getData("AdressDb.txt"));
    appendPhone(getData("PhoneDb.txt"));
    appendAccount(getData("AccountDb.txt"));
    appendTransaction(getData("TransactionsDb.txt"));
    appendTransactiontype(getData("TransactionTypeDb.txt"));

    clientsNum = clientStorage.values().size();
  }

  private List<String[]> getData(String file) {
    List<String[]> returnStr = new ArrayList<String[]>();

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream(file), "UTF-8"))) {

      while (true) {
        String line = br.readLine();
        if (line == null) break;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");

        returnStr.add(splitLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return returnStr;
  }

  @SuppressWarnings("unused")
  private void appendPerson(List<String[]> data) {
    for (String[] splitLine : data) {
      PersonDot p = new PersonDot();
      p.id = splitLine[1].trim();
      String[] ap = splitLine[2].trim().split("\\s+");
      String[] fio = splitLine[3].trim().split("\\s+");
      p.accountName = ap[0];
      p.password = ap[1];
      p.surname = fio[0];
      p.name = fio[1];
      if (fio.length > 2) p.patronymic = fio[2];
      personStorage.put(p.id, p);
    }
  }

  private void appendClient (List<String[]> data) {
    for (String [] splitLine : data) {
      ClientDot c = new ClientDot();
      c.id = splitLine[0].trim();
      String[] fio = splitLine[1].trim().split("\\s+");
      c.surname = fio[1];
      c.name = fio[0];
      c.gender = splitLine[2].trim();
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
      try {
        c.birth_date = format.parse(splitLine[3].trim());
      } catch (ParseException e) {
        e.printStackTrace();
      }
      c.charmID = splitLine[4].trim();
      if (fio.length > 2) c.patronymic = fio[2]; else c.patronymic = "";
      clientStorage.put(c.id, c);
    }
  }

  private void appendAdress(List<String[]> data) {
    for (String[] splitLine : data) {
      AdressDot adr = new AdressDot();
      adr.id = String.valueOf(this.adressStorage.values().size() + 1);
      adr.clientID = splitLine[0].trim();
      adr.adressType = splitLine[1].trim();
      adr.street = splitLine[2].trim();
      adr.house = splitLine[3].trim();
      adr.flat = splitLine[4].trim();
      adressStorage.put(adr.id, adr);
    }
  }

  private void appendPhone(List<String[]> data) {
    for (String[] splitLine : data) {
      PhoneDot ph = new PhoneDot();
      ph.clientID = splitLine[0].trim();
      ph.number = splitLine[1].trim();
      ph.phoneType = splitLine[2].trim();
      phoneStorage.put(ph.number, ph);
    }
  }

  private void appendAccount(List<String[]> data) {
    for (String[] splitLine : data) {
      AccountDot acc = new AccountDot();
      acc.id = splitLine[0].trim();
      acc.clientID = splitLine[1].trim();
      acc.money = Float.parseFloat(splitLine[2].trim());
      acc.number = splitLine[3].trim();
      acc.registered_at = Timestamp.valueOf(splitLine[4].trim());
      accountStorage.put(acc.id, acc);
    }
  }

  private void appendTransaction(List<String[]> data) {
    for (String[] splitLine : data) {
      TransactionDot tr = new TransactionDot();
      tr.id = splitLine[0].trim();
      tr.accountID = splitLine[1].trim();
      tr.money = Float.parseFloat(splitLine[2].trim());
      tr.finished_at = Timestamp.valueOf(splitLine[3].trim());
      tr.transactionTypeID = splitLine[4].trim();
      transactionStorage.put(tr.id, tr);
    }
  }

  private void appendTransactiontype(List<String[]> data) {
    for (String[] splitLine : data) {
      TransactionType trtp = new TransactionType();
      trtp.id = splitLine[0].trim();
      trtp.code = splitLine[1].trim();
      trtp.name = splitLine[2].trim();
      transactionTypeStorage.put(trtp.id, trtp);
    }
  }

  private void appendCharm(List<String[]> data) {
    for (String[] splitLine : data) {
      CharmDot ch = new CharmDot();
      ch.id = splitLine[0].trim();
      ch.name = splitLine[1].trim();
      ch.description = splitLine[2].trim();
      ch.energy = Float.parseFloat(splitLine[3].trim());
      charmStorage.put(ch.id, ch);
    }
  }

  public String addNewCLient(ClientToSave clientToSave) {

    clientsNum++;
    this.clientID = "c" + String.valueOf(clientsNum);
    clientToSave.id = this.clientID;

    ClientDot c = new ClientDot();
    c.id = clientToSave.id;
    c.surname = clientToSave.surname;
    c.name = clientToSave.name;
    c.patronymic = clientToSave.patronymic;
    c.gender = clientToSave.gender;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    try {
      c.birth_date = format.parse(clientToSave.birth_date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    for (CharmDot charmDot : charmStorage.values()) {
      if (charmDot.name.equals(clientToSave.charm)) {
        c.charmID = charmDot.id;
      }
    }

    clientStorage.put(c.id, c);

    addNewPhones(clientToSave);
    addNewAdresses(clientToSave);

    return c.id;
  }
  public void addNewPhones(ClientToSave clientToSave) {
    PhoneDot ph;

    for(String phone : clientToSave.workPhone) {
      ph = new PhoneDot();
      ph.clientID = clientToSave.id;
      ph.number = phone;
      ph.phoneType = "WORK";
      phoneStorage.put(ph.number, ph);
    }

    for(String phone : clientToSave.homePhone) {
      ph = new PhoneDot();
      ph.clientID = clientToSave.id;
      ph.number = phone;
      ph.phoneType = "HOME";
      phoneStorage.put(ph.number, ph);
    }

      for(String phone : clientToSave.mobilePhones) {
        ph = new PhoneDot();
        ph.clientID = clientToSave.id;
        ph.number = phone;
        ph.phoneType = "MOBILE";
        phoneStorage.put(ph.number, ph);
      }
  }
  public void addNewAdresses(ClientToSave clientToSave) {
    AdressDot adr = new AdressDot();
    adr.id = String.valueOf(this.adressStorage.values().size() + 1);
    adr.clientID = clientToSave.id;
    adr.adressType = "REG";
    adr.street = clientToSave.rAdressStreet;
    adr.house = clientToSave.rAdressHouse;
    adr.flat = clientToSave.rAdressFlat;
    adressStorage.put(adr.id, adr);

    adr = new AdressDot();
    adr.id = String.valueOf(this.adressStorage.values().size() + 1);
    adr.clientID = clientToSave.id;
    adr.adressType = "FACT";
    adr.street = clientToSave.fAdressStreet;
    adr.house = clientToSave.fAdressHouse;
    adr.flat = clientToSave.fAdressFlat;
    adressStorage.put(adr.id, adr);
  }

  public String updateClient(ClientToSave clientToSave) {
    ClientDot c = this.clientStorage.get(clientToSave.id);
    c.id = clientToSave.id;
    c.surname = clientToSave.surname;
    c.name = clientToSave.name;
    c.patronymic = clientToSave.patronymic;
    c.gender = clientToSave.gender;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    try {
      c.birth_date = format.parse(clientToSave.birth_date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    for (CharmDot charmDot : charmStorage.values()) {
      if (charmDot.name.equals(clientToSave.charm)) {
        c.charmID = charmDot.id;
      }
    }

    clientStorage.put(c.id, c);

    addNewPhones(clientToSave);
    addNewAdresses(clientToSave);

    return c.id;
  }

  public void removeClient(String clientID) {
    clientStorage.remove(clientID);

    for (AdressDot adr : this.adressStorage.values()) {
      if (adr.clientID.equals(clientID)) {
        adressStorage.values().remove(adr);
        break;
      }
    }

    for (PhoneDot phoneDot : phoneStorage.values()) {
      if (phoneDot.clientID.equals(clientID)) {
        phoneStorage.values().remove(phoneDot);
        break;
      }
    }
  }

  public ClientDetails getEditableClientInfo(String clientID) {

    ClientDetails clientDetails = clientStorage.get(clientID).toClientDetails();
    clientDetails.charm = charmStorage.get(clientStorage.get(clientID).charmID).name;

    for (AdressDot adressDot : adressStorage.values()) {
      if (Objects.equals(adressDot.clientID, clientID)) {
        adressDot.toClientDetails(clientDetails);
      }
    }

    for (PhoneDot phoneDot : phoneStorage.values()) {
      if (phoneDot.clientID.equals(clientID)) {
        phoneDot.toClientDetails(clientDetails);
      }
    }

    return clientDetails;
  }
}
