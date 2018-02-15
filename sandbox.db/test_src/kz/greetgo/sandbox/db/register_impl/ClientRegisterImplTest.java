package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Test
  void getClientInfoListTest() {

    this.clientTestDao.get().clear();

    List<ClientDot> clients = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      ClientDot cd = this.rndClientDot();
      this.clientTestDao.get().insertClientDot(cd);
      clients.add(cd);
    }

    int limit = 10;
    int page = 10;
    String filter = "";
    String orderBy = "name";
    int desc = 0;

    long size = this.clientRegister.get().getClientsSize(null);
    //
    //
    List<ClientRecord> result = this.clientRegister.get().getClientInfoList(limit, page, filter, orderBy, desc);
    //
    //

    List<ClientDot> filtered = clients.stream().filter(o -> o.getFIO().toLowerCase().contains(filter)).collect(Collectors.toList());
    filtered.sort((o1, o2) -> {
      if (desc == 1) {
        ClientDot tmp = o1;
        o1 = o2;
        o2 = tmp;
      }
      String fio1 = o1.name;
      String fio2 = o2.name;
      return fio1.compareTo(fio2);
    });

    assertThat(result).isNotEmpty();
    for (int i = 0; i < result.size(); i++) {
      // FIXME: 2/14/18 сравнивай все поля, не только айди
      ClientDot expected = filtered.get(i + page * limit);
      ClientRecord target = result.get(i);
      assertThat(expected.id).isEqualTo(target.id);

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
      List<String> list = Arrays.asList(filter.trim().split(" "));
      long expected = clients.stream().filter(o -> list.stream().anyMatch(x -> o.getFIO().toLowerCase().contains(x.toLowerCase()))).count();
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
    return c;
  }


}
