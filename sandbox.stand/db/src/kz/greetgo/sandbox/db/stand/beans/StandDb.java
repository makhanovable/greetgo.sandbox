package kz.greetgo.sandbox.db.stand.beans;

import com.sun.org.apache.bcel.internal.generic.FLOAD;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<String, Client> clientStorage = new HashMap<>();
  public final Map<String, Adress> adressStorage = new HashMap<>();
  public final Map<String, Charm> charmStorage = new HashMap<>();
  public final Map<String, Transaction> transactionStorage = new HashMap<>();
  public final Map<String, TransactionType> transactionTypeStorage = new HashMap<>();
  public final Map<String, Phone> phoneStorage = new HashMap<>();
  public final Map<String, Account> accountStorage = new HashMap<>();

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
      Client c = new Client();
      c.id = splitLine[0].trim();
      String[] fio = splitLine[1].trim().split("\\s+");
      c.surname = fio[0];
      c.name = fio[1];
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
      Adress adr = new Adress();
      adr.clientID = splitLine[0].trim();
      adr.adressType = splitLine[1].trim();
      adr.street = splitLine[2].trim();
      adr.house = splitLine[3].trim();
      adr.flat = splitLine[4].trim();
      adressStorage.put(adr.clientID, adr);
    }
  }

  private void appendPhone(List<String[]> data) {
    for (String[] splitLine : data) {
      Phone ph = new Phone();
      ph.clientID = splitLine[0].trim();
      ph.number = splitLine[1].trim();
      ph.phoneType = splitLine[2].trim();
      phoneStorage.put(ph.clientID, ph);
    }
  }

  private void appendAccount(List<String[]> data) {
    for (String[] splitLine : data) {
      Account acc = new Account();
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
      Transaction tr = new Transaction();
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
      Charm ch = new Charm();
      ch.id = splitLine[0].trim();
      ch.name = splitLine[1].trim();
      ch.description = splitLine[2].trim();
      ch.energy = Float.parseFloat(splitLine[3].trim());
      charmStorage.put(ch.id, ch);
    }
  }

  public void addNewCLient(String clientInfo) {
    List<String[]> client = new ArrayList<String[]>();
    String[] splitLine = clientInfo.split(";");
    client.add(splitLine);

    for (Charm charm : charmStorage.values()) {
      if (charm.name.equals(splitLine[4].trim())) {
        splitLine[4] = charm.id;
      }
    }

    clientInfo = String.join(";", splitLine);
    addClientToDb(clientInfo);
    appendClient(client);
  }
  private void addClientToDb(String client) {
    BufferedWriter bw = null;
    FileWriter fw = null;

    try {
      File file = new File("/Users/sanzharburumbay/Documents/Greetgo_Internship/greetgo.sandbox/sandbox.stand/db/src/kz/greetgo/sandbox/db/stand/beans/ClientDb.txt");
      fw = new FileWriter(file.getAbsoluteFile(), true);
      bw = new BufferedWriter(fw);
      bw.write(client);
      bw.write("\n");

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bw != null)
          bw.close();
        if (fw != null)
          fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void addNewPhones(String phones) {
    String[] phonesArray = phones.split(",");

    for (String phone : phonesArray) {
      addPhoneToDb(phone);

      List<String[]> phonesList = new ArrayList<String[]>();
      String[] splitLine = phone.split(";");
      phonesList.add(splitLine);
      appendPhone(phonesList);
    }
  }
  private void addPhoneToDb (String phone) {
    BufferedWriter bw = null;
    FileWriter fw = null;

    try {
      File file = new File("/Users/sanzharburumbay/Documents/Greetgo_Internship/greetgo.sandbox/sandbox.stand/db/src/kz/greetgo/sandbox/db/stand/beans/PhoneDb.txt");
      fw = new FileWriter(file.getAbsoluteFile(), true);
      bw = new BufferedWriter(fw);
      bw.write(phone);
      bw.write("\n");

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bw != null)
          bw.close();
        if (fw != null)
          fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void addNewAdresses(String adresses) {
    System.out.println(adresses);
    String[] adressesArray = adresses.split(",");

    for (String adress : adressesArray) {
      addToAdressDb(adress);

      List<String[]> adressesList = new ArrayList<String[]>();
      String[] splitLine = adress.split(";");
      adressesList.add(splitLine);
      appendPhone(adressesList);
    }
  }
  private void addToAdressDb (String adress) {
    BufferedWriter bw = null;
    FileWriter fw = null;

    try {
      File file = new File("/Users/sanzharburumbay/Documents/Greetgo_Internship/greetgo.sandbox/sandbox.stand/db/src/kz/greetgo/sandbox/db/stand/beans/AdressDb.txt");
      fw = new FileWriter(file.getAbsoluteFile(), true);
      bw = new BufferedWriter(fw);
      bw.write(adress);
      bw.write("\n");

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bw != null)
          bw.close();
        if (fw != null)
          fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
