package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumberToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Test
  void clientRecordListAndReportFilterByNameTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    ClientDot clientDot1 = rndClientDot();
    this.clientTestDao.get().insertClientDot(clientDot1);

    ClientDot assertion = rndClientDot();
    String filter = assertion.name;
    assertion.name += RND.str(10);
    this.clientTestDao.get().insertClientDot(assertion);

    {//getClientRecordList

      //
      //
      List<ClientRecord> list = this.clientRegister.get().getClientRecordList(10, 0, filter, null, 0);
      //
      //

      assertThat(list).hasSize(1);
      ClientRecord result = list.get(0);
      assertThat(result.name).isEqualTo(assertion.name);
    }


    {//Report

      ClientReportTestView testView = new ClientReportTestView();

      //
      //
      this.clientRegister.get().genClientRecordListReport(assertion.name, null, 0, testView);
      //
      //

      assertThat(testView.rows).hasSize(1);
      ClientRecord result = testView.rows.get(0);
      assertThat(result.name).isEqualTo(assertion.name);
    }
  }

  @Test
  void clientRecordListAndReportFilterBySurnameTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    ClientDot clientDot1 = rndClientDot();
    this.clientTestDao.get().insertClientDot(clientDot1);

    ClientDot assertion = rndClientDot();
    this.clientTestDao.get().insertClientDot(assertion);

    {//getClientRecordList

      //
      //
      List<ClientRecord> list = this.clientRegister.get().getClientRecordList(10, 0, assertion.surname, null, 0);
      //
      //

      assertThat(list).hasSize(1);
      ClientRecord result = list.get(0);
      assertThat(result.surname).isEqualTo(assertion.surname);
    }


    {//genClientRecordListReport

      ClientReportTestView testView = new ClientReportTestView();

      //
      //
      this.clientRegister.get().genClientRecordListReport(assertion.surname, null, 0, testView);
      //
      //

      assertThat(testView.rows).hasSize(1);
      ClientRecord result = testView.rows.get(0);
      assertThat(result.surname).isEqualTo(assertion.surname);
    }
  }

  @Test
  void clientRecordListAndReportFilterByPatronymicTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    ClientDot clientDot1 = rndClientDot();
    this.clientTestDao.get().insertClientDot(clientDot1);

    ClientDot assertion = rndClientDot();
    this.clientTestDao.get().insertClientDot(assertion);

    {//Client Record List

      //
      //
      List<ClientRecord> list = this.clientRegister.get().getClientRecordList(10, 0, assertion.patronymic, null, 0);
      //
      //

      assertThat(list).hasSize(1);
      ClientRecord result = list.get(0);
      assertThat(result.patronymic).isEqualTo(assertion.patronymic);
    }


    {//Report

      ClientReportTestView testView = new ClientReportTestView();

      //
      //
      this.clientRegister.get().genClientRecordListReport(assertion.patronymic, null, 0, testView);
      //
      //

      assertThat(testView.rows).hasSize(1);
      ClientRecord result = testView.rows.get(0);
      assertThat(result.patronymic).isEqualTo(assertion.patronymic);
    }
  }


  @Test
  void clientRecordListAndReportOrderByAgeAscTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    insertNRndClients(10);

    {//Client Record List

      //
      //
      List<ClientRecord> list = this.clientRegister.get().getClientRecordList(10, 0, null, "age", 0);
      //
      //

      assertThat(list).isNotEmpty();
      for (int i = 0; i < list.size() - 1; i++) {
        ClientRecord record = list.get(i);
        ClientRecord nextRecord = list.get(i + 1);
        assertThat(record.age).isLessThanOrEqualTo(nextRecord.age);
      }

    }

    {//Report

      ClientReportTestView testView = new ClientReportTestView();

      //
      //
      this.clientRegister.get().genClientRecordListReport(null, "age", 0, testView);
      //
      //


      assertThat(testView.rows).isNotEmpty();
      for (int i = 0; i < testView.rows.size() - 1; i++) {
        ClientRecord record = testView.rows.get(i);
        ClientRecord nextRecord = testView.rows.get(i + 1);

        assertThat(record.age).isLessThanOrEqualTo(nextRecord.age);
      }

    }
  }

  @Test
  void clientRecordListAndReportOrderByAgeDescTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    insertNRndClients(10);

    {//Client Record List

      //
      //
      List<ClientRecord> list = this.clientRegister.get().getClientRecordList(10, 0, null, "age", 1);
      //
      //

      assertThat(list).isNotEmpty();
      for (int i = 0; i < list.size() - 1; i++) {
        ClientRecord record = list.get(i);
        ClientRecord nextRecord = list.get(i + 1);
        assertThat(record.age).isGreaterThanOrEqualTo(nextRecord.age);
      }

    }

    {//Report

      ClientReportTestView testView = new ClientReportTestView();

      //
      //
      this.clientRegister.get().genClientRecordListReport(null, "age", 1, testView);
      //
      //


      assertThat(testView.rows).isNotEmpty();
      for (int i = 0; i < testView.rows.size() - 1; i++) {
        ClientRecord record = testView.rows.get(i);
        ClientRecord nextRecord = testView.rows.get(i + 1);

        assertThat(record.age).isGreaterThanOrEqualTo(nextRecord.age);
      }

    }
  }


  @Test
  void clientRecordListLimitOffsetHeadTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    int numberOfClients = 100;
    List<ClientDot> clients = insertNRndClients(numberOfClients);


    int limit = 10, page = 0;
    //
    //
    List<ClientRecord> list = this.clientRegister.get().getClientRecordList(limit, page, null, "fio", 0);
    //
    //

    assertThat(list).isNotEmpty();

    clients.sort(Comparator.comparing(ClientDot::getFIO));
    int startIndex = page * limit;
    int endIndex = startIndex + limit;
    endIndex = endIndex >= numberOfClients ? numberOfClients : endIndex;
    clients = clients.subList(startIndex, endIndex);

    assertThat(list).hasSize(clients.size());

    for (int i = 0; i < list.size(); i++) {
      ClientRecord record = list.get(i);
      ClientDot expected = clients.get(i);

      assertThat(record.id).isEqualTo(expected.id);
    }

  }

  @Test
  void clientRecordListLimitOffsetMiddleTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    int numberOfClients = 100;
    List<ClientDot> clients = insertNRndClients(numberOfClients);


    int limit = 10;
    int page = numberOfClients / limit / 2;
    //
    //
    List<ClientRecord> list = this.clientRegister.get().getClientRecordList(limit, page, null, "fio", 0);
    //
    //

    assertThat(list).isNotEmpty();

    clients.sort(Comparator.comparing(ClientDot::getFIO));
    int startIndex = page * limit;
    int endIndex = startIndex + limit;
    endIndex = endIndex >= numberOfClients ? numberOfClients : endIndex;
    clients = clients.subList(startIndex, endIndex);

    assertThat(list).hasSize(clients.size());


    for (int i = 0; i < list.size(); i++) {
      ClientRecord record = list.get(i);
      ClientDot expected = clients.get(i);

      assertThat(record.id).isEqualTo(expected.id);
    }

  }


  @Test
  void clientRecordListLimitOffsetLastPageWithNotReachingTheLimitTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    int numberOfClients = 10;
    List<ClientDot> clients = insertNRndClients(numberOfClients);


    int limit = 4;
    int page = 2; //third page  expected [8 : 12) but must return [8:10]
    //
    //
    List<ClientRecord> list = this.clientRegister.get().getClientRecordList(limit, page, null, "fio", 0);
    //
    //

    assertThat(list).isNotEmpty();

    clients.sort(Comparator.comparing(ClientDot::getFIO));
    int startIndex = page * limit;
    int endIndex = startIndex + limit;
    endIndex = endIndex >= numberOfClients ? numberOfClients : endIndex;

    clients = clients.subList(startIndex, endIndex);

    assertThat(list).hasSize(clients.size());

    for (int i = 0; i < list.size(); i++) {
      ClientRecord record = list.get(i);
      ClientDot expected = clients.get(i);

      assertThat(record.id).isEqualTo(expected.id);
    }

  }


  private List<ClientDot> insertNRndClients(int number) {
    List<ClientDot> clients = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      ClientDot clientDot = rndClientDot();
      clients.add(clientDot);
      this.clientTestDao.get().insertClientDot(clientDot);
    }
    return clients;
  }


  @Test
  void NumberOfClientWithoutFilterTest() {
    this.clientTestDao.get().clear();

    List<ClientDot> clients = insertNRndClients(100);

    //
    //
    long result = this.clientRegister.get().getNumberOfClients(null);
    //
    //

    assertThat(result).isEqualTo(clients.size());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void NumberOfClientWithFilterTest() {
    this.clientTestDao.get().clear();

    insertNRndClients(100);
    String rndtext = "asdlkjflalfasdkjfnaВЕЛИКИЙРАНДОМывлафдывтадфыsgfsdfgsdfgdsfgsdfgв";
    ClientDot clientDot = rndClientDot();
    ClientDot clientDot2 = rndClientDot();
    clientDot.name = rndtext;
    clientDot2.name = rndtext;
    this.clientTestDao.get().insertClientDot(clientDot);
    this.clientTestDao.get().insertClientDot(clientDot2);

    //
    //
    long result = this.clientRegister.get().getNumberOfClients(clientDot.name);
    //
    //

    assertThat(result).isEqualTo(2);
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
    int deleted = this.clientRegister.get().removeClients(toDeleteList);
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

    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);

    List<String> toDeleteList = new ArrayList<>();
    toDeleteList.add(cd.id);

    //
    //
    int deleted = this.clientRegister.get().removeClients(toDeleteList);
    //
    //

    assertThat(deleted).isEqualTo(toDeleteList.size());
    ClientDetail clientDetail = this.clientTestDao.get().detail(cd.id, true);
    assertThat(clientDetail).isNull();
  }


  @Test
  public void updateClientDeleteNumberTest() {
    this.clientTestDao.get().clear();

    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);

    ClientPhoneNumberDot number1 = rndPhoneNumber(cd.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(number1); // +1 number


    ClientToSave clientToSave = cd.toClientToSave();
    clientToSave.numbersToDelete = new ArrayList<>();
    clientToSave.numbersToDelete.add(number1.toClientPhoneNumber());  // -1 number

    //
    //
    this.clientRegister.get().addOrUpdate(clientToSave);
    //
    //

    ClientDetail clientDetail = this.clientTestDao.get().detail(clientToSave.id, true);
    this.assertClientDetail(clientDetail, new ClientDot(clientToSave));


    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(clientToSave.id);
    assertThat(numberList).isEmpty();
  }

  @Test
  public void updateClientPhoneNumber() {

    this.clientTestDao.get().clear();
    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);

    ClientPhoneNumberDot numberToInsert = rndPhoneNumber(cd.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(numberToInsert);

    ClientToSave clientToSave = cd.toClientToSave();

    ClientPhoneNumberToSave toEdited = numberToInsert.toClientPhoneNumberToSave();
    toEdited.oldNumber = toEdited.number;
    toEdited.number = RND.str(10);
    clientToSave.numbersToSave = new ArrayList<>();
    clientToSave.numbersToSave.add(toEdited);

    //
    //
    this.clientRegister.get().addOrUpdate(clientToSave);
    //
    //

    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(clientToSave.id);

    assertThat(numberList.stream().anyMatch(o -> o.number.equals(toEdited.number))).isTrue();
  }

  @Test
  public void updateClientTest() throws Exception {
    throw new RuntimeException("Do it");
  }

  @Test
  public void checkFillClientRecord() throws Exception {
    throw new RuntimeException("Do it");
  }

  @Test
  public void updateClientAddressTest() {
    this.clientTestDao.get().clear();
    ClientDot cd = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(cd);

    ClientAddressDot registerAddress = rndAddress(cd.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(registerAddress);

    ClientToSave clientToSave = cd.toClientToSave();
    ClientAddressDot addressToUpdate = new ClientAddressDot();
    addressToUpdate.client = cd.id;
    addressToUpdate.type = AddressType.REG;
    addressToUpdate.street = "черноморская";
    addressToUpdate.house = "12b";
    addressToUpdate.flat = "подвал";
    clientToSave.registerAddress = addressToUpdate.toClientAddress();

    //
    //
    this.clientRegister.get().addOrUpdate(clientToSave);
    //
    //

    ClientAddress target = this.clientTestDao.get().getAddres(clientToSave.id, AddressType.REG);
    this.assertClientAddress(target, addressToUpdate);
  }


  @Test
  public void addClientTest() {
    this.clientTestDao.get().clear();

    ClientDot cd = this.rndClientDot();
    cd.id = null;
    ClientAddressDot registerAddress = rndAddress(null, AddressType.REG);
    ClientPhoneNumberDot mobileNumber = rndPhoneNumber(null, PhoneNumberType.MOBILE);

    ClientToSave clientToSave = cd.toClientToSave();
    clientToSave.registerAddress = registerAddress.toClientAddress();
    clientToSave.numbersToSave = new ArrayList<>();
    clientToSave.numbersToSave.add(mobileNumber.toClientPhoneNumberToSave());

    //
    //
    this.clientRegister.get().addOrUpdate(clientToSave);
    //
    //


    List<ClientDetail> list = this.clientTestDao.get().getAllByName(cd.name);
    assertThat(list).hasSize(1);
    ClientDetail target = list.get(0);

    this.assertClientDetail(target, cd);

    ClientAddress regAddress = this.clientTestDao.get().getAddres(target.id, AddressType.REG);
    this.assertClientAddress(regAddress, registerAddress);

    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(target.id);
    assertThat(numberList).isNotEmpty();
    assertThat(numberList).hasSize(clientToSave.numbersToSave.size());

    assertPhoneNumber(numberList.get(0), mobileNumber);

  }


  @Test
  public void getDetailTest() throws Exception {

    this.clientTestDao.get().clear();

    ClientDot c = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(c);

    ClientPhoneNumberDot number1 = rndPhoneNumber(c.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(number1);

    ClientAddressDot registerAddress = rndAddress(c.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(registerAddress);


    //
    //
    ClientDetail detail = this.clientRegister.get().getDetail(c.id);
    //
    //

    this.assertClientDetail(detail, c);
    assertThat(detail.phoneNumbers).isNotNull();
    assertThat(detail.phoneNumbers).hasSize(1);
    this.assertPhoneNumber(detail.phoneNumbers.get(0), number1);

    assertThat(detail.actualAddress).isNull();
    assertThat(detail.registerAddress).isNotNull();
    this.assertClientAddress(detail.registerAddress, registerAddress);
  }


  private void assertPhoneNumber(ClientPhoneNumber target, ClientPhoneNumberDot assertion) {
    assertThat(target).isNotNull();
    assertThat(target.number).isEqualTo(assertion.number);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private void assertClientAddress(ClientAddress target, ClientAddressDot assertion) {
    assertThat(target).isNotNull();
    assertThat(target.street).isEqualTo(assertion.street);
    assertThat(target.house).isEqualTo(assertion.house);
    assertThat(target.flat).isEqualTo(assertion.flat);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private void assertClientDetail(ClientDetail target, ClientDot assertion) {

    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    assertThat(sdf.format(target.birthDate)).isEqualTo(sdf.format(assertion.birthDate));
    assertThat(target.charm).isEqualTo(assertion.charm);
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


}
