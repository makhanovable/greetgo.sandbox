package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.stand.model.ReportInstanceDot;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<Integer, CharmDot> charmStorage = new HashMap<>();
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Long, ClientDot> clientStorage = new HashMap<>();
  public AtomicLong curClientId = new AtomicLong(0);
  public final Map<String, ReportInstanceDot> clientListReportStorage = new HashMap<>();

  @Override
  public void afterInject() throws Exception {
    this.parseCharms();
    this.parsePersons();
    this.parseClients();
    this.generateClients();
  }

  private void parseCharms() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbCharmData.txt"), "UTF-8"))) {

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
          case "CHARM":
            appendCharm(splitLine, line, lineNo);
            break;

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  private void appendCharm(String[] splitLine, String line, int lineNo) {
    CharmDot charmDot = new CharmDot();

    charmDot.id = Integer.parseInt(splitLine[1].trim());
    charmDot.name = splitLine[2].trim();
    charmDot.isDisabled = false;

    this.charmStorage.put(charmDot.id, charmDot);
  }

  private void parsePersons() throws Exception {
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

  private void parseClients() throws Exception {
    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbClientData.txt"), "UTF-8"))) {

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
          case "CLIENT":
            appendClient(splitLine, line, lineNo);
            break;

          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }

      curClientId.set(lineNo);
    }
  }

  @SuppressWarnings("unused")
  private void appendClient(String[] splitLine, String line, int lineNo) {
    ClientDot c = new ClientDot();
    c.id = lineNo - 1;
    c.surname = splitLine[1].trim();
    c.name = splitLine[2].trim();
    c.patronymic = splitLine[3].trim();
    c.gender = toGender(Integer.parseInt(splitLine[4].trim()));
    c.birthDate = this.generateDate();
    c.charm = this.generateCharm();
    c.factualAddressInfo = this.generateAddressInfo(AddressType.FACTUAL);
    c.registrationAddressInfo = this.generateAddressInfo(AddressType.REGISTRATION);
    c.phones = this.generatePhones();

    ClientDot.generateAgeAndBalance(c);

    clientStorage.put(c.id, c);
  }

  private void generateClients() {
    for (int i = 0; i < 500; i++) {
      ClientDot c = new ClientDot();
      c.id = clientStorage.size();
      c.surname = RND.str(RND.plusInt(5) + 5);
      c.name = RND.str(RND.plusInt(5) + 5);
      c.patronymic = RND.str(RND.plusInt(5) + 5);
      c.gender = toGender(RND.plusInt(Gender.values().length));
      c.birthDate = this.generateDate();
      c.charm = this.generateCharm();
      c.factualAddressInfo = this.generateAddressInfo(AddressType.FACTUAL);
      c.registrationAddressInfo = this.generateAddressInfo(AddressType.REGISTRATION);
      c.phones = this.generatePhones();

      ClientDot.generateAgeAndBalance(c);

      clientStorage.put(c.id, c);
    }
  }

  // https://stackoverflow.com/a/3985467
  private String generateDate() {
    long time = -946771200000L + (Math.abs(new Random().nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));
    Date date = new Date(time);

    return new SimpleDateFormat(Util.datePattern).format(date);
  }

  private Charm generateCharm() {
    Object[] values = charmStorage.values().toArray();
    CharmDot charmDot = (CharmDot) values[new Random().nextInt(values.length)];

    return charmDot.toCharm();
  }

  private AddressInfo generateAddressInfo(AddressType addressType) {
    Random random = new Random();
    AddressInfo ret = new AddressInfo();

    ret.type = addressType;
    ret.street = RND.str(random.nextInt(10) + 5);
    ret.house = RND.str(random.nextInt(3) + 2);
    ret.flat = RND.str(random.nextInt(3) + 2);

    return ret;
  }

  private List<Phone> generatePhones() {
    Random random = new Random();
    List<Phone> ret = new ArrayList<>();
    int n = random.nextInt(3) + 1;

    for (int i = 0; i < n; i++) {
      Phone phone = new Phone();
      phone.number = "+" + RND.intStr(11);
      phone.type = toPhoneType(random.nextInt(PhoneType.values().length));

      ret.add(phone);
    }

    return ret;
  }

  private static Gender toGender(int i) {
    return Gender.values()[i];
  }

  private static PhoneType toPhoneType(int i) {
    switch (i) {
      case 0:
        return PhoneType.HOME;
      case 1:
        return PhoneType.WORK;
      case 2:
        return PhoneType.MOBILE;
      case 3:
        return PhoneType.EMBEDDED;
      default:
        return PhoneType.OTHER;
    }
  }
}
