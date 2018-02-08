package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();

  public Map<Integer, ClientDot> clientStorage = new HashMap<>();
  public Integer nextClientId = 0;
  public final Map<Integer, CharmDot> charmStorage = new HashMap<>();

  public Map<Integer, List<ClientPhoneNumberDot>> clientPhoneNumberStorage = new HashMap<>();
  public Map<Integer, List<ClientAddressDot>> clientAddressStorage = new HashMap<>();

  public final Map<Integer, ClientAccountDot> clientAccountStorage = new HashMap<>();
  public final Map<Integer, ClientAccountTransactionDot> clientAccountTransactionStorage = new HashMap<>();
  public final Map<Integer, TransactionTypeDot> transactionTypeStorage = new HashMap<>();

  @SuppressWarnings("unchecked")
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

    String[] charms = {"Lazy", "Loyal", "Patient", "Loving"};

    for (int i = 0; i < charms.length; i++) {
      CharmDot charm = new CharmDot();
      charm.id = i;
      charm.name = charms[i];
      charm.description = "some description";
      charm.energy = new Random().nextFloat();
      this.charmStorage.put(charm.id, charm);
    }

    try {
      ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("initClients.ser"));
      this.clientStorage = (HashMap) ois.readObject();
      ois.close();
      ois = new ObjectInputStream(getClass().getResourceAsStream("initClientPhoneNumbers.ser"));
      this.clientPhoneNumberStorage = (HashMap) ois.readObject();
      ois.close();
      ois = new ObjectInputStream(getClass().getResourceAsStream("initClientAddresses.ser"));
      this.clientAddressStorage = (HashMap) ois.readObject();
      ois.close();
    } catch (Exception e) {
      System.out.println(e.toString());
    }

    nextClientId = this.clientStorage.size() + 1000;
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
}
