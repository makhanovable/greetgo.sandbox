package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Integer, ClientDot> clientStorage = new HashMap<>();
  public final Map<Integer, CharmDot> charmStorage = new HashMap<>();


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

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }

    CharmDot charm = new CharmDot();
    charm.id = 0;
    charm.name = "ленивый";
    charm.description = "sdcfsgaeg";
    charm.energy = 1541563f;

    this.charmStorage.put(charm.id, charm);

    ClientDot clientDot = new ClientDot();
    clientDot.id = 0;
    clientDot.name = "Dauren";
    clientDot.surname = "Amze";
    clientDot.patronymic = "D.";
    clientDot.charmId = 0;
    clientDot.birthDate = new Date("13/09/1996");
    clientDot.gender = GenderType.MALE;

    this.clientStorage.put(clientDot.id, clientDot);
  }

//  private void addClient() {
//
//  }

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
}
