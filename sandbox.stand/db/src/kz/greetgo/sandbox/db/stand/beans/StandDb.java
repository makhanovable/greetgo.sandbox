package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.db.stand.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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


    String[] names = {"Brent", "Joshua", "Tomothy", "Douglas", "Johnny", "Gwendolyn", "Timmothy", "Lynn", "Annette", "Eduardo",
      "Manuel", "Sean", "Jared", "Chloe", "Carla", "Ray", "Karl", "Carl", "Riley", "Michele",
      "Lydia", "Evan", "Letitia", "Melvin", "Jamie", "Jonathan", "Sean", "Brooklyn", "Margie", "Potter",
      "Ruby", "Deann", "Ronnie", "Alyssa", "Julia", "Leroy", "Eddie", "Johnni", "Max", "Joshua",
      "Marian", "Patrick", "Rafael", "Henry", "Terry", "Cameron", "Lester", "Andy", "Gilbert", "Raul",
      "Dylan", "Allen", "Priscilla", "Jacob", "Lucy", "Jackie", "William", "Hilda", "Andre", "Javier",
      "Lewis", "Samantha", "Lee", "Louise", "Franklin", "Ethan", "Jimmie", "Vicki", "Jeffery", "Cody",
      "Jennifer", "Guy", "Dave", "Madison", "Judy", "Annette", "Pamela", "Ian", "Ronald", "Stephen",
      "Heather", "Claire", "Tony", "Marion", "Stephen", "Edwin", "Kirk", "Dennis", "Liam", "Delores"};

    Random rnd = new Random(666);

    int items = (int) Math.floor(names.length / 3);

    for (int i = 0; i < items; i++) {
      ClientDot clientDot = new ClientDot();
      clientDot.id = i;
      clientDot.name = names[i];
      clientDot.surname = names[(i + items)];
      clientDot.patronymic = names[(i + items * 2)];
      clientDot.charmId = rnd.nextInt(charms.length);
      clientDot.birthDate = rndDate(rnd);
      clientDot.gender = rnd.nextInt(2) == 0 ? GenderType.MALE : GenderType.FEMALE;
      this.clientStorage.put(i, clientDot);

      List<ClientAddressDot> addrList = new ArrayList<>();
      ClientAddressDot clientAddress = new ClientAddressDot();
      clientAddress.street = names[rnd.nextInt(names.length)];
      clientAddress.house = rnd.nextInt(100) + 1 + "";
      clientAddress.flat = rnd.nextInt(100) + 1 + "";
      clientAddress.clientId = i;
      clientAddress.type = rnd.nextInt(2) == 0 ? AddressType.FACT : AddressType.REG;
      addrList.add(clientAddress);
      this.clientAddressStorage.put(i, addrList);

      List<ClientPhoneNumberDot> numberList = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        ClientPhoneNumberDot number = new ClientPhoneNumberDot();
        number.number = "" + (7000000000l + rnd.nextLong() % 1000000000l);
        number.type = rnd.nextInt(2) == 0 ? PhoneNumberType.MOBILE : PhoneNumberType.WORK;
        number.clientId = i;
        numberList.add(number);
      }
      this.clientPhoneNumberStorage.put(i, numberList);
    }
    nextClientId = this.clientStorage.size() + 1000;
  }

  private Date rndDate(Random rnd) {
    return new Date(-946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000)));
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
