package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.db.stand.model.*;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;

import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final Map<Integer, ClientDot> clientStorage = new HashMap<>();
  public Integer nextClientId = 0;
  public final Map<Integer, CharmDot> charmStorage = new HashMap<>();

  public final Map<Integer, List<ClientPhoneNumberDot>> clientPhoneNumberStorage = new HashMap<>();
  public final Map<Integer, List<ClientAddressDot>> clientAddressStorage = new HashMap<>();

  public final Map<Integer, ClientAccountDot> clientAccountStorage = new HashMap<>();
  public final Map<Integer, ClientAccountTransactionDot> clientAccountTransactionStorage = new HashMap<>();
  public final Map<Integer, TransactionTypeDot> transactionTypeStorage = new HashMap<>();

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

    String [] charms = {"ленивый", "Loyal", "Patient", "Loving"};

    for(int i = 0; i < charms.length; i++) {
      CharmDot charm = new CharmDot();
      charm.id = i;
      charm.name = charms[i];
      charm.description = "some description";
      charm.energy = new Random().nextFloat();
      this.charmStorage.put(charm.id, charm);
    }

    ClientDot clientDot = new ClientDot();
    clientDot.id = 0;
    clientDot.name = "Dauren";
    clientDot.surname = "Amze";
    clientDot.patronymic = "D.";
    clientDot.charmId = 0;
    clientDot.birthDate = new Date("09/13/1996");
    clientDot.gender = GenderType.MALE;



    this.clientStorage.put(clientDot.id, clientDot);

    clientDot = new ClientDot();
    clientDot.id = 1;
    clientDot.name = "Harry";
    clientDot.surname = "Potter";
    clientDot.patronymic = "James";
    clientDot.charmId = 2;
    clientDot.birthDate = new Date("01/15/2000");
    clientDot.gender = GenderType.MALE;

    this.clientStorage.put(clientDot.id, clientDot);


    List<ClientPhoneNumberDot> numberList = new ArrayList<>();

    ClientPhoneNumberDot number1 = new ClientPhoneNumberDot();
    number1.clientId = 0;
    number1.number = "7770022296";
    number1.type = PhoneNumberType.MOBILE;
    numberList.add(number1);

    number1 = new ClientPhoneNumberDot();
    number1.clientId = 0;
    number1.number = "1461315645";
    number1.type = PhoneNumberType.WORK;
    numberList.add(number1);

    number1 = new ClientPhoneNumberDot();
    number1.clientId = 0;
    number1.number = "4561351311";
    number1.type = PhoneNumberType.HOME;
    numberList.add(number1);
    this.clientPhoneNumberStorage.put(0, numberList);

    List<ClientAddressDot> addresses = new ArrayList<>();

    ClientAddressDot clientAddress = new ClientAddressDot();
    clientAddress.clientId = 1;
    clientAddress.street = "кислая";
    clientAddress.house = "11";
    clientAddress.flat = "1";
    clientAddress.type = AddressType.FACT;
    addresses.add(clientAddress);

    clientAddress = new ClientAddressDot();
    clientAddress.clientId = 1;
    clientAddress.street = "dsvdgagwwaswg";
    clientAddress.house = "541";
    clientAddress.flat = "2";
    clientAddress.type = AddressType.REG;
    addresses.add(clientAddress);
    this.clientAddressStorage.put(1, addresses);


    nextClientId = this.clientStorage.size();
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
