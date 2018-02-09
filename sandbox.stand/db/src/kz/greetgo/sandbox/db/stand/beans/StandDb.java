package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.sandbox.db.stand.tools.IdGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@SuppressWarnings("unused")
@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();

  public Map<String, ClientDot> clientStorage = new HashMap<>();
  public final Map<String, CharmDot> charmStorage = new HashMap<>();

  public Map<String, List<ClientPhoneNumberDot>> clientPhoneNumberStorage = new HashMap<>();
  public Map<String, List<ClientAddressDot>> clientAddressStorage = new HashMap<>();

  public final Map<String, ClientAccountDot> clientAccountStorage = new HashMap<>();
  public final Map<String, ClientAccountTransactionDot> clientAccountTransactionStorage = new HashMap<>();
  public final Map<String, TransactionTypeDot> transactionTypeStorage = new HashMap<>();

  IdGenerator gen = new IdGenerator();

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
    List<String> charmsIds = new ArrayList<>();

    for (String charm : charms) {
      CharmDot charmdot = new CharmDot();
      charmdot.id = this.gen.newId();
      charmdot.name = charm;
      charmdot.description = "some description";
      charmdot.energy = new Random().nextFloat();

      charmsIds.add(charmdot.id);
      this.charmStorage.put(charmdot.id, charmdot);
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
      clientDot.id = this.gen.newId();
      clientDot.name = names[i];
      clientDot.surname = names[(i + items)];
      clientDot.patronymic = names[(i + items * 2)];
      clientDot.charmId = charmsIds.get(rnd.nextInt(charms.length));
      clientDot.birthDate = rndDate(rnd);
      clientDot.gender = rnd.nextInt(2) == 0 ? GenderType.MALE : GenderType.FEMALE;
      this.clientStorage.put(clientDot.id, clientDot);

      List<ClientAddressDot> addrList = new ArrayList<>();
      ClientAddressDot clientAddress = new ClientAddressDot();
      clientAddress.street = names[rnd.nextInt(names.length)];
      clientAddress.house = rnd.nextInt(100) + 1 + "";
      clientAddress.flat = rnd.nextInt(100) + 1 + "";
      clientAddress.clientId = clientDot.id;
      clientAddress.type = rnd.nextInt(2) == 0 ? AddressType.FACT : AddressType.REG;
      addrList.add(clientAddress);
      this.clientAddressStorage.put(clientDot.id, addrList);

      List<ClientPhoneNumberDot> numberList = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        ClientPhoneNumberDot number = new ClientPhoneNumberDot();
        number.number = "" + (7000000000L + rnd.nextLong() % 1000000000L);
        number.type = rnd.nextInt(2) == 0 ? PhoneNumberType.MOBILE : PhoneNumberType.WORK;
        number.clientId = clientDot.id;
        numberList.add(number);
      }
      this.clientPhoneNumberStorage.put(clientDot.id, numberList);
    }

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
