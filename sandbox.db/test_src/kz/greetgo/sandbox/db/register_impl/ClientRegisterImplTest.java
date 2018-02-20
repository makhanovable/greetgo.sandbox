package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.dao.AccountTetsDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Calendar;


import static org.fest.assertions.api.Assertions.assertThat;


public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<AccountTetsDao> accountTetsDao;


  private List<ClientRecord> fromClientDotListToRecordList(List<ClientDot> clientDots, Map<String, List<ClientAccountDot>> accounts) {
    List<ClientRecord> records = new ArrayList<>();
    for (ClientDot clientDot : clientDots) {
      ClientRecord cr = clientDot.toClientRecord();
      List<ClientAccountDot> accList = accounts.get(clientDot.id);

      float max = accList.get(0).money;
      float min = accList.get(0).money;
      float total = 0;
      for (ClientAccountDot cad : accList) {
        total += cad.money;
        if (max < cad.money)
          max = cad.money;
        if (min > cad.money)
          min = cad.money;
      }
      cr.totalAccountBalance = total;
      cr.maximumBalance = max;
      cr.minimumBalance = min;
      records.add(cr);
    }
    return records;
  }

  @Test
  void getClientInfoListTest() {
    this.clientTestDao.get().clear();

    //init data
    List<ClientDot> clients = new ArrayList<>();
    Map<String, List<ClientAccountDot>> accountDotMap = new HashMap<>();
    Map<String, ClientDot> clientDotMap = new HashMap<>();

    for (int i = 0; i < 100; i++) {
      ClientDot cd = this.rndClientDot();
      cd.birthDate.setTime(-2177474400000L + i * 31536000000L); //1900, 1900 + 1, 1900 + 2 ...

      this.clientTestDao.get().insertClientDot(cd);
      List<ClientAccountDot> accList = new ArrayList<>();

      for (int j = 0; j < 2 + RND.plusInt(5); j++) {
        ClientAccountDot cad = this.rndClientAccountDot(cd.id);
        this.accountTetsDao.get().insertAccaount(cad);
        accList.add(cad);
      }

      clients.add(cd);
      clientDotMap.put(cd.id, cd);
      accountDotMap.put(cd.id, accList);
    }


    //init inputs
    ClientDot rndClient = clients.get(RND.plusInt(clients.size()));
    int[] limits = {20, 10, 0};
    int[] pages = {10, 5, 2, 1, 0};
    String[] orderBys = {null, "", "age", "totalAccountBalance", "maximumBalance", "minimumBalance"};
    int[] orders = {0, 1};
    String[] filters = {null, rndClient.name + " " + rndClient.surname, rndClient.patronymic, rndClient.getFIO(), "a", "ab", RND.str(3)};

    for (int limit : limits) {
      for (int page : pages) {
        for (String orderBy : orderBys) {
          for (int order : orders) {
            for (String filter : filters) {

              //
              //
              List<ClientRecord> result = this.clientRegister.get().getClientInfoList(limit, page, filter, orderBy, order);
              //
              //

              if (limit == 0) {
                assertThat(result).isEmpty();
                continue;
              }

              List<ClientRecord> expectedList;
              List<ClientDot> filteredClientDots;

              //filtering
              if (filter != null && !filter.isEmpty()) {
                String[] filterTokens = filter.trim().split(" ");
                filteredClientDots = clients.stream().filter(o -> Arrays.stream(filterTokens).anyMatch(y -> o.getFIO().toLowerCase().contains(y.toLowerCase()))).collect(Collectors.toList());
              } else
                filteredClientDots = clients;

              expectedList = this.fromClientDotListToRecordList(filteredClientDots, accountDotMap);

              //sorting
              String ob = orderBy == null ? "default" : orderBy;

              expectedList.sort((o1, o2) -> {
                if (order == 1) {
                  ClientRecord tmp = o1;
                  o1 = o2;
                  o2 = tmp;
                }
                switch (ob) {
                  case "age":
                    return Integer.compare(o1.age, o2.age);
                  case "totalAccountBalance":
                    return Double.compare(o1.totalAccountBalance, o2.totalAccountBalance);
                  case "maximumBalance":
                    return Double.compare(o1.maximumBalance, o2.maximumBalance);
                  case "minimumBalance":
                    return Double.compare(o1.minimumBalance, o2.minimumBalance);
                  default:
                    String fio1 = o1.getFIO().toLowerCase();
                    String fio2 = o2.getFIO().toLowerCase();
                    return fio1.compareTo(fio2);
                }

              });


              int fromIndex = limit * page;
              int endIndex = fromIndex + limit <= expectedList.size() ? fromIndex + limit : expectedList.size();

              if (endIndex <= fromIndex)
                expectedList = new ArrayList<>();
              else
                expectedList = expectedList.subList(fromIndex, endIndex);


              //test
              assertThat(result).hasSize(expectedList.size());

              for (int i = 0; i < result.size(); i++) {
                ClientRecord target = result.get(i);
                ClientRecord assertion = expectedList.get(i);

                assertThat(clientDotMap).containsKey(target.id);

                assertThat(target.id).isEqualTo(assertion.id);
                assertThat(target.age).isEqualTo(assertion.age);
                assertThat(target.charm).isEqualTo(assertion.charm);
                assertThat(target.surname).isEqualTo(assertion.surname);
                assertThat(target.patronymic).isEqualTo(assertion.patronymic);
                assertThat(target.name).isEqualTo(assertion.name);

                assertThat(target.maximumBalance).isEqualTo(assertion.maximumBalance);
                assertThat(target.minimumBalance).isEqualTo(assertion.minimumBalance);
                assertThat(target.totalAccountBalance).isEqualTo(assertion.totalAccountBalance);

              }

            }
          }
        }
      }
    }


  }


  @Test
  void getClientsSizeTest() {
    this.clientTestDao.get().clear();

    List<ClientDot> clients = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      ClientDot cd = this.rndClientDot();
      this.clientTestDao.get().insertClientDot(cd);
      clients.add(cd);
    }

    String[] filters = {
      null,
      clients.get(RND.plusInt(clients.size())).name,
      clients.get(RND.plusInt(clients.size())).surname,
      clients.get(RND.plusInt(clients.size())).patronymic,
      clients.get(RND.plusInt(clients.size())).getFIO()};

    for (String filter : filters) {

      //
      //
      long result = this.clientRegister.get().getClientsSize(filter);
      //
      //

      if (filter == null) {
        assertThat(result).isEqualTo(clients.size());
        continue;
      }
      List<String> filterTokens = Arrays.asList(filter.trim().split(" "));

      long expected = clients.stream().filter(o -> filterTokens.stream().anyMatch(x -> o.getFIO().toLowerCase().contains(x.toLowerCase()))).count();
      assertThat(result).isEqualTo(expected);
    }

  }

  @Test
  void removeClientsTest() {
    this.clientTestDao.get().clear();
    List<String> toDeleteList = new ArrayList<>();

    for (int i = 0; i < RND.plusInt(100); i++) {
      ClientDot cd = this.rndClientDot();
      this.clientTestDao.get().insertClientDot(cd);
      toDeleteList.add(cd.id);
    }

    //
    //
    int deleted = this.clientRegister.get().remove(toDeleteList);
    //
    //

    assertThat(deleted).isEqualTo(toDeleteList.size());
    for (String id : toDeleteList) {
      ClientDetail clientDetail = this.clientTestDao.get().detail(id, true);
      assertThat(clientDetail).isNull();
    }
  }

  @Test
  void removeOneClientTest() {
    this.clientTestDao.get().clear();
    List<String> ids = new ArrayList<>();

    for (int i = 0; i < RND.plusInt(100); i++) {
      ClientDot cd = this.rndClientDot();
      this.clientTestDao.get().insertClientDot(cd);
      ids.add(cd.id);
    }
    List<String> toDeleteList = new ArrayList<>();
    String id = ids.get(0);
    toDeleteList.add(id);

    //
    //
    int deleted = this.clientRegister.get().remove(toDeleteList);
    //
    //

    assertThat(deleted).isEqualTo(toDeleteList.size());

    ClientDetail clientDetail = this.clientTestDao.get().detail(id, true);
    assertThat(clientDetail).isNull();


  }


  @Test
  public void updateClientTest() {
    this.clientTestDao.get().clear();
    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);
    ClientAddressDot actualAddress = rndAddress(cd.id, AddressType.FACT);
    ClientAddressDot registerAddress = rndAddress(cd.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(actualAddress);
    this.clientTestDao.get().insertAddress(registerAddress);

    ClientToSave clientToSave = rndClientToSave(cd.id); // +ClientDetail +2addresses +3numbers
    {
      ClientPhoneNumberDot number1 = rndPhoneNumber(cd.id, PhoneNumberType.WORK);
      this.clientTestDao.get().insertPhone(number1); // number directly insterted to db
      clientToSave.numbersToDelete.add(number1.toClientPhoneNumber());  // -1 number

      //
      //
      this.clientRegister.get().addOrUpdate(clientToSave);
      //
      //

      ClientDetail clientDetail = this.clientTestDao.get().detail(clientToSave.id, true);
      this.assertClientDetail(clientDetail, new ClientDot(clientToSave));

      ClientAddress regAddress = this.clientTestDao.get().getAddres(clientToSave.id, AddressType.REG);
      ClientAddress actAddress = this.clientTestDao.get().getAddres(clientToSave.id, AddressType.FACT);
      this.assertClientAddres(actAddress, new ClientAddressDot(clientToSave.id, clientToSave.actualAddress));
      this.assertClientAddres(regAddress, new ClientAddressDot(clientToSave.id, clientToSave.registerAddress));

      List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(clientToSave.id);

      assertThat(numberList).isNotEmpty();
      assertThat(numberList).hasSize(clientToSave.numersToSave.size());
      assertThat(numberList.stream().anyMatch(o -> o.number.equals(number1.number))).isFalse();
    }

  }

  @Test
  public void updateClientEditedPhoneNumber() {
    this.clientTestDao.get().clear();
    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);
    ClientAddressDot actualAddress = rndAddress(cd.id, AddressType.FACT);
    ClientAddressDot registerAddress = rndAddress(cd.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(actualAddress);
    this.clientTestDao.get().insertAddress(registerAddress);

    ClientPhoneNumberDot numberToInsert = rndPhoneNumber(cd.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(numberToInsert);

    ClientToSave test2 = rndClientToSave(cd.id);

    ClientPhoneNumberToSave toEdited = numberToInsert.toClientPhoneNumberToSave();

    toEdited.oldNumber = toEdited.number;
    toEdited.number = RND.str(10);
    test2.numersToSave.add(toEdited);

    //
    //
    this.clientRegister.get().addOrUpdate(test2);
    //
    //

    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(test2.id);
    assertThat(numberList.stream().anyMatch(o -> o.number.equals(toEdited.number))).isTrue();
  }

  @Test
  public void addClientTest() {
    this.clientTestDao.get().clear();
    ClientToSave client = rndClientToSave(null);

    //
    //
    this.clientRegister.get().addOrUpdate(client);
    //
    //

    ClientDetail clientDetail = this.clientTestDao.get().detail(client.id, true);
    this.assertClientDetail(clientDetail, new ClientDot(client));
    ClientAddress regAddress = this.clientTestDao.get().getAddres(client.id, AddressType.REG);
    ClientAddress actAddress = this.clientTestDao.get().getAddres(client.id, AddressType.FACT);
    this.assertClientAddres(actAddress, new ClientAddressDot(client.id, client.actualAddress));
    this.assertClientAddres(regAddress, new ClientAddressDot(client.id, client.registerAddress));

    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(client.id);
    assertThat(numberList.isEmpty()).isFalse();
    assertThat(numberList.size()).isEqualTo(client.numersToSave.size());

    for (ClientPhoneNumberToSave cpn : client.numersToSave) {
      assertThat(numberList.stream().anyMatch(o -> o.number.equals(cpn.number) && o.type.equals(cpn.type) && o.client.equals(cpn.client))).isTrue();
    }
  }


  @Test
  public void getDetailTest() throws Exception {

    this.clientTestDao.get().clear();
    ClientDot c = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(c);
    ClientPhoneNumberDot number1 = rndPhoneNumber(c.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(number1);
    ClientAddressDot actualAddress = rndAddress(c.id, AddressType.FACT);
    ClientAddressDot registerAddress = rndAddress(c.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(actualAddress);
    this.clientTestDao.get().insertAddress(registerAddress);


    //
    //
    ClientDetail detail = this.clientRegister.get().detail(c.id);
    //
    //

    this.assertClientDetail(detail, c);
    assertThat(detail.phoneNumbers).isNotNull();
    assertThat(detail.actualAddress).isNotNull();
    assertThat(detail.registerAddress).isNotNull();
    assertThat(detail.phoneNumbers.size()).isEqualTo(1);
    this.assertClientAddres(detail.actualAddress, actualAddress);
    this.assertClientAddres(detail.registerAddress, registerAddress);
    this.assertPhoneNumber(detail.phoneNumbers.get(0), number1);

  }

  private ClientToSave rndClientToSave(String id) {
    ClientToSave client = new ClientToSave();
    client.id = id;
    client.name = RND.str(10);
    client.surname = RND.str(10);
    client.patronymic = RND.str(10);
    client.gender = RND.someEnum(GenderType.values());
    client.birthDate = RND.dateYears(-100, 0);
    client.charm = RND.str(10);

    client.actualAddress = rndAddress(id, AddressType.FACT).toClientAddress();
    client.registerAddress = rndAddress(id, AddressType.REG).toClientAddress();

    client.numbersToDelete = new ArrayList<>();
    client.numersToSave = new ArrayList<>();

    client.numersToSave.add(rndPhoneNumber(id, PhoneNumberType.WORK).toClientPhoneNumberToSave());
    client.numersToSave.add(rndPhoneNumber(id, PhoneNumberType.MOBILE).toClientPhoneNumberToSave());
    client.numersToSave.add(rndPhoneNumber(id, PhoneNumberType.HOME).toClientPhoneNumberToSave());

    return client;
  }

  private void assertPhoneNumber(ClientPhoneNumber target, ClientPhoneNumberDot assertion) {
    if (target == null && assertion == null)
      return;
    assertThat(target).isNotNull();
    assertThat(target.client).isEqualTo(assertion.client);
    assertThat(target.number).isEqualTo(assertion.number);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private void assertClientAddres(ClientAddress target, ClientAddressDot assertion) {
    if (target == null && assertion == null)
      return;
    assertThat(target).isNotNull();
    assertThat(target.client).isEqualTo(assertion.client);
    assertThat(target.street).isEqualTo(assertion.street);
    assertThat(target.house).isEqualTo(assertion.house);
    assertThat(target.flat).isEqualTo(assertion.flat);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private ClientAddressDot rndAddress(String id, AddressType type) {
    ClientAddressDot address = new ClientAddressDot();
    address.client = id;
    address.type = type;

    address.street = RND.str(10);
    address.house = RND.str(10);
    return address;
  }

  private ClientPhoneNumberDot rndPhoneNumber(String clientId, PhoneNumberType type) {
    ClientPhoneNumberDot number = new ClientPhoneNumberDot();
    number.client = clientId;
    number.type = type;
    number.number = idGenerator.get().newId();
    return number;
  }

  private void assertClientDetail(ClientDetail target, ClientDot assertion) {
    if (target == null && assertion == null)
      return;

    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    assertThat(sdf.format(target.birthDate)).isEqualTo(sdf.format(assertion.birthDate));
    assertThat(target.charm).isEqualTo(assertion.charm);
    assertThat(target.id).isEqualTo(assertion.id);
  }

  private ClientDot rndClientDot() {
    ClientDot c = new ClientDot();
    c.id = idGenerator.get().newId();
    c.name = idGenerator.get().newId();
    c.surname = idGenerator.get().newId();
    c.patronymic = idGenerator.get().newId();
    c.charm = RND.str(10);
    c.gender = RND.someEnum(GenderType.values());
    c.birthDate = RND.dateYears(-100, 0);

    Calendar cal = Calendar.getInstance();
    cal.setTime(c.birthDate);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    c.birthDate = cal.getTime();
    return c;
  }

  private ClientAccountDot rndClientAccountDot(String client) {
    ClientAccountDot cad = new ClientAccountDot();
    cad.money = (float) RND.plusDouble(1000000f, 10);
    cad.id = idGenerator.get().newId();
    cad.number = idGenerator.get().newId();
    cad.client = client;
    return cad;
  }


}
