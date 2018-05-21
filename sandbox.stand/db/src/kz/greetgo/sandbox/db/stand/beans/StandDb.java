package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.AddressType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.stand.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Integer, ClientDot> clientStorage = new HashMap<>();
  public final Map<Integer, AddressDot> addressStorage = new HashMap<>();
  public final Map<Integer, PhoneDot> phoneStorage = new HashMap<>();
  public final Map<Integer, CharmDot> charmStorage = new HashMap<>();
  public final Map<Integer, AccountDot> accountStorage = new HashMap<>();

  @Override
  public void afterInject() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbInitData.txt"), "UTF-8"))) {

      int lineNo = 0;

      while (true) {
        String line = br.readLine();
        if (line == null) break;
        lineNo++;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");

        String command = splitLine[0].trim();
        switch (command) {
          case "PERSON":
            appendPerson(splitLine, line, lineNo);
            break;
//          case "ACCOUNT_INFO":
//            appendAccountInfo(splitLine);
//            break;
          case "CLIENT":
            appendClient(splitLine);
            break;
          case "PHONE":
            appendPhone(splitLine);
            break;
          case "ADDRESS":
            appendAddress(splitLine);
            break;
          case "CHARM":
            appendCharm(splitLine);
            break;
          case "ACCOUNT":
            appendAccount(splitLine);
            break;
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  private void appendAccount(String[] splitLine) {
    AccountDot a = new AccountDot();
    a.id = Integer.parseInt(splitLine[1].trim());
    a.clientId = Integer.parseInt(splitLine[2].trim());
    a.money = Float.parseFloat(splitLine[3].trim());
    a.number = splitLine[4].trim();

    try {
      Date parsedDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(splitLine[5].trim());
      a.registeredAt = new java.sql.Timestamp(parsedDate.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    accountStorage.put(a.id, a);
  }

  private void appendCharm(String[] splitLine) {
    CharmDot c = new CharmDot();
    c.id = Integer.parseInt(splitLine[1].trim());
    c.name = splitLine[2].trim();
    c.description = splitLine[3].trim();
    c.energy = Float.parseFloat(splitLine[4].trim());

    charmStorage.put(c.id, c);
  }

  private void appendAddress(String[] splitLine) {
    AddressDot a = new AddressDot();
    a.id = Integer.parseInt(splitLine[1].trim());
    a.clientId = Integer.parseInt(splitLine[2].trim());
    a.addressType = AddressType.valueOf(splitLine[3].trim());

    String[] fullAdd = splitLine[4].trim().split(",");
    a.street = fullAdd[0].trim();
    a.house = fullAdd[1].trim();
    if(fullAdd.length > 2) a.flat = fullAdd[2].trim();

    addressStorage.put(a.id, a);
  }

  private void appendPhone(String[] splitLine) {
    PhoneDot p = new PhoneDot();
    p.id = Integer.parseInt(splitLine[1].trim());
    p.clientId = Integer.parseInt(splitLine[2].trim());
    p.number = splitLine[3].trim();
    p.type = PhoneType.valueOf(splitLine[4].trim());

    phoneStorage.put(p.id, p);
  }

  private void appendClient(String[] splitLine) {
    ClientDot c = new ClientDot();
    c.id = Integer.parseInt(splitLine[1].trim());
    c.gender = Gender.valueOf(splitLine[3].trim());
    c.charmId = Integer.parseInt(splitLine[5].trim());

    try {
      c.birthDate = new SimpleDateFormat("dd-MM-yyyy").parse(splitLine[4].trim());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    String[] fio = splitLine[2].trim().split("\\s+");
    c.name = fio[0].trim();
    c.surname = fio[1].trim();
    if (fio.length > 2) c.patronymic = fio[2].trim();

    clientStorage.put(c.id, c);
  }

  @SuppressWarnings("unused")
  private void appendPerson(String[] splitLine, String line, int lineNo) {
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

//  private void appendAccountInfo(String[] splitLine) {
//    AccountInfoDot acc = new AccountInfoDot();
//    acc.id = Integer.parseInt(splitLine[1].trim());
//    acc.fullName = splitLine[2].trim();
//    acc.charmId = splitLine[3].trim();
//    acc.age = Integer.parseInt(splitLine[4].trim());
//    acc.totalAccBalance = Float.parseFloat(splitLine[5].trim());
//    acc.maxAccBalance = Float.parseFloat(splitLine[6].trim());
//    acc.minAccBalance = Float.parseFloat(splitLine[7].trim());
//
//    accountInfoStorage.put(acc.id, acc);
//  }
}
