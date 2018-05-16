package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  private void appendAccount(String[] splitLine) {
    AccountDot a = new AccountDot();
    a.id = Integer.parseInt(splitLine[1]);
    a.clientId = Integer.parseInt(splitLine[2]);
    a.money = Float.parseFloat(splitLine[3]);
    a.number = splitLine[4];
    a.registeredAt = Timestamp.valueOf(splitLine[5]);

    accountStorage.put(a.id, a);
  }

  private void appendCharm(String[] splitLine) {
    CharmDot c = new CharmDot();
    c.id = Integer.parseInt(splitLine[1]);
    c.name = splitLine[2];
    c.description = splitLine[3];
    c.energy = Float.parseFloat(splitLine[4]);

    charmStorage.put(c.id, c);
  }

  private void appendAddress(String[] splitLine) {
     AddressDot a = new AddressDot();
     a.id = Integer.parseInt(splitLine[1]);
     a.addressType = splitLine[2];
     a.clientId = Integer.parseInt(splitLine[3]);
     a.street = splitLine[4];
     a.house = splitLine[5];
     a.flat = splitLine[6];

     addressStorage.put(a.id, a);
  }

  private void appendPhone(String[] splitLine) {
    PhoneDot p = new PhoneDot();
    p.id = Integer.parseInt(splitLine[1]);
    p.clientId = Integer.parseInt(splitLine[2]);
    p.number = splitLine[3];
    p.type = splitLine[4];

    phoneStorage.put(p.id, p);
  }

  private void appendClient(String[] splitLine) {
    ClientDot c = new ClientDot();
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
//    acc.charm = splitLine[3].trim();
//    acc.age = Integer.parseInt(splitLine[4].trim());
//    acc.totalAccBalance = Float.parseFloat(splitLine[5].trim());
//    acc.maxAccBalance = Float.parseFloat(splitLine[6].trim());
//    acc.minAccBalance = Float.parseFloat(splitLine[7].trim());
//
//    accountInfoStorage.put(acc.id, acc);
//  }
}
