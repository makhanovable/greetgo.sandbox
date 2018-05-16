package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.db.stand.model.AccountInfoDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Integer, AccountInfoDot> accountInfoStorage = new HashMap<>();

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
          case "ACCOUNT_INFO":
            appendAccountInfo(splitLine);
            break;
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
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

  private void appendAccountInfo(String[] splitLine) {
    AccountInfoDot acc = new AccountInfoDot();
    acc.id = Integer.parseInt(splitLine[1].trim());
    acc.fullName = splitLine[2].trim();
    acc.charm = splitLine[3].trim();
    acc.age = Integer.parseInt(splitLine[4].trim());
    acc.totalAccBalance = Float.parseFloat(splitLine[5].trim());
    acc.maxAccBalance = Float.parseFloat(splitLine[6].trim());
    acc.minAccBalance = Float.parseFloat(splitLine[7].trim());

    accountInfoStorage.put(acc.id, acc);
  }
}
