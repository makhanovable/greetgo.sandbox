package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ClientCommonTest extends ParentTestNg {
  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  public ClientRecordSetHelper getRecordList_default() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordSetHelper clientRecordSetHelper = new ClientRecordSetHelper();
    clientRecordSetHelper.clientRecordSet = new HashSet<>();
    clientRecordSetHelper.clientRecordSet.add(
      this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(
      this.insertClient("а", "б", "в", Gender.EMPTY.name(), LocalDate.now(), 0, charmHelperList));
    for (int i = 0; i < 5; i++)
      clientRecordSetHelper.clientRecordSet.add(
        this.insertClient(charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(
      this.insertClient("Игорев", "Игорь", "Игоревич", Gender.EMPTY.name(), LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(
      this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(), 0, charmHelperList));

    clientRecordSetHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordSetHelper.clientRecordSet.size() + 5, ColumnSortType.NONE, false, "");

    return clientRecordSetHelper;
  }

  public ClientRecordListHelper getRecordList_sortAgeAscend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int age = RND.plusInt(50) + 10;
      clientRecordListHelper.clientRecordList.add(this.insertClient("", "", "", Gender.EMPTY.name(),
        LocalDate.now().minusYears(age), 0, charmHelperList));
    }
    clientRecordListHelper.clientRecordList.sort((o1, o2) -> Integer.compare(o1.age, o2.age));

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordListHelper.clientRecordList.size() + 10, ColumnSortType.AGE, true, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortAgeDescend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      int age = RND.plusInt(50) + 10;
      clientRecordListHelper.clientRecordList.add(this.insertClient("", "", "", Gender.EMPTY.name(),
        LocalDate.now().minusYears(age), 0, charmHelperList));
    }
    clientRecordListHelper.clientRecordList.sort((o1, o2) -> Integer.compare(o2.age, o1.age));

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordListHelper.clientRecordList.size() + 10, ColumnSortType.AGE, false, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortTotalAccountBalanceAscend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> totalMoneyList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      float totalMoney = 0;
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = (float) RND.plusDouble(100000, 2);
        totalMoney += money;
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      totalMoneyList.add(totalMoney);
    }
    totalMoneyList.sort((o1, o2) -> Float.compare(o1, o2));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).totalAccountBalance = totalMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest =
      this.clientRecordRequestBuilder(0, clientRecordListHelper.clientRecordList.size() + 10,
        ColumnSortType.TOTALACCOUNTBALANCE, true, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortTotalAccountBalanceDescend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> totalMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      float totalMoney = 0;
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = (float) RND.plusDouble(200000, 2);
        totalMoney += money;
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      totalMoneyList.add(totalMoney);
    }
    totalMoneyList.sort((o1, o2) -> Float.compare(o2, o1));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).totalAccountBalance = totalMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordListHelper.clientRecordList.size() + 10, ColumnSortType.TOTALACCOUNTBALANCE, false, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortMaxAccountBalanceAscend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> maxMoneyList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(5) + 1; j++) {
        float money = (float) RND.plusDouble(100000, 2);
        moneyList.add(money);
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      maxMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).max().getAsDouble());
    }
    maxMoneyList.sort((o1, o2) -> Float.compare(o1, o2));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).maxAccountBalance = maxMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      maxMoneyList.size() + 10, ColumnSortType.MAXACCOUNTBALANCE, true, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortMaxAccountBalanceDescend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> maxMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(4) + 1; j++) {
        float money = (float) RND.plusDouble(200000, 2);
        moneyList.add(money);
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      maxMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).max().getAsDouble());
    }
    maxMoneyList.sort((o1, o2) -> Float.compare(o2, o1));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).maxAccountBalance = maxMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      maxMoneyList.size() + 10, ColumnSortType.MAXACCOUNTBALANCE, false, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortMinAccountBalanceAscend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> minMoneyList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(6) + 1; j++) {
        float money = (float) RND.plusDouble(100000, 2);
        moneyList.add(money);
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      minMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).min().getAsDouble());
    }
    minMoneyList.sort((o1, o2) -> Float.compare(o1, o2));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).minAccountBalance = minMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      minMoneyList.size() + 10, ColumnSortType.MINACCOUNTBALANCE, true, "");

    return clientRecordListHelper;
  }

  public ClientRecordListHelper getRecordList_sortMinAccountBalanceDescend() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordListHelper clientRecordListHelper = new ClientRecordListHelper();
    clientRecordListHelper.clientRecordList = new ArrayList<>();
    List<Float> minMoneyList = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      ClientRecord clientRecord = this.insertClient(charmHelperList);
      clientRecordListHelper.clientRecordList.add(clientRecord);
      List<Float> moneyList = new ArrayList<>();
      for (int j = 0; j < RND.plusInt(3) + 1; j++) {
        float money = (float) RND.plusDouble(200000, 2);
        moneyList.add(money);
        this.insertClientAccount(clientRecord.id, money, RND.str(11), new Timestamp(0));
      }
      minMoneyList.add((float) moneyList.stream().mapToDouble(m -> m).min().getAsDouble());
    }
    minMoneyList.sort((o1, o2) -> Float.compare(o2, o1));
    for (int i = 0; i < clientRecordListHelper.clientRecordList.size(); i++)
      clientRecordListHelper.clientRecordList.get(i).minAccountBalance = minMoneyList.get(i).toString();

    clientRecordListHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      minMoneyList.size() + 10, ColumnSortType.MINACCOUNTBALANCE, false, "");

    return clientRecordListHelper;
  }

  public ClientRecordSetHelper getRecordList_filter() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordSetHelper clientRecordSetHelper = new ClientRecordSetHelper();
    clientRecordSetHelper.clientRecordSet = new HashSet<>();
    String dummyName = "null";
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("Далана", "квала", "Смук", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("русская", "буква", "Игоревич", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("Квентин", "джон", "Нурланович", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);

    clientRecordSetHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordSetHelper.clientRecordSet.size(), ColumnSortType.NONE, false, "кв");

    return clientRecordSetHelper;
  }

  public ClientRecordSetHelper getRecordList_filterOnEmptyName() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    ClientRecordSetHelper clientRecordSetHelper = new ClientRecordSetHelper();
    clientRecordSetHelper.clientRecordSet = new HashSet<>();
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("", "", "", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("asd", "", "dsa", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("NULL", "null", "", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));

    clientRecordSetHelper.clientRecordRequest =
      this.clientRecordRequestBuilder(0, clientRecordSetHelper.clientRecordSet.size(), ColumnSortType.NONE, false, "");

    return clientRecordSetHelper;
  }

  public ClientRecordSetHelper getRecordList_filterWithIgnoredCase() {
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    ClientRecordSetHelper clientRecordSetHelper = new ClientRecordSetHelper();
    clientRecordSetHelper.clientRecordSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("игорев", "квала", "Смук", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("русская", "Игорь", "Игоревич", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    clientRecordSetHelper.clientRecordSet.add(this.insertClient("Квентин", "джон", "Пигоревич", Gender.EMPTY.name(),
      LocalDate.now(), 0, charmHelperList));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);

    clientRecordSetHelper.clientRecordRequest = this.clientRecordRequestBuilder(0,
      clientRecordSetHelper.clientRecordSet.size() + 3, ColumnSortType.NONE, false, "иГоР");

    return clientRecordSetHelper;
  }

  public static class ClientRecordSetHelper {
    Set<ClientRecord> clientRecordSet;
    ClientRecordRequest clientRecordRequest;
  }

  public static class ClientRecordListHelper {
    List<ClientRecord> clientRecordList;
    ClientRecordRequest clientRecordRequest;
  }

  public static class CharmHelper {
    int id;
    String name;
    String description;
    float energy;
  }

  public CharmHelper declareAndInsertCharm(String name, String description, float energy) {
    int id = clientTestDao.get().selectSeqIdNextValueTableCharm();
    clientTestDao.get().insertCharm(id, name, description, energy);

    CharmHelper charmHelper = new CharmHelper();
    charmHelper.id = id;
    charmHelper.name = name;
    charmHelper.description = description;
    charmHelper.energy = energy;

    return charmHelper;
  }

  public List<CharmHelper> declareAndInsertCharms() {
    List<CharmHelper> charmHelperList = new ArrayList<>();

    charmHelperList.add(this.declareAndInsertCharm("Не указан", "Неизвестно", 0f));
    charmHelperList.add(this.declareAndInsertCharm("Спокойный", "Само спокойствие", 10f));
    charmHelperList.add(this.declareAndInsertCharm("Буйный", "Лучше лишний раз не трогать", 30f));
    charmHelperList.add(this.declareAndInsertCharm("Загадочный", "О чем он думает?", 8f));
    charmHelperList.add(this.declareAndInsertCharm("Открытый", "С ним приятно общаться!", 20f));
    charmHelperList.add(this.declareAndInsertCharm("Понимающий", "Он всегда выслушает", 15f));
    charmHelperList.add(this.declareAndInsertCharm("Консервативный", "Скучно...", 5f));

    return charmHelperList;
  }

  public void insertClientPhone(long client, String number, PhoneType type) {
    clientTestDao.get().insertClientPhone(client, number, type.name());
  }

  public void insertClientAddress(long client, AddressType addressType, String street, String house, String flat) {
    clientTestDao.get().insertClientAddr(client, addressType.name(), street, house, flat);
  }

  public long insertClientAccount(long clientId, float money, String code, Timestamp timestamp) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClientAccount();
    clientTestDao.get().insertClientAccount(id, clientId, money, code, timestamp);
    return id;
  }

  public ClientRecord insertClient(List<CharmHelper> charmHelperList) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    String surname = RND.str(RND.plusInt(5) + 5);
    String name = RND.str(RND.plusInt(5) + 5);
    String patronymic = RND.str(RND.plusInt(5) + 5);
    Gender gender = Gender.values()[RND.plusInt(Gender.values().length)];
    Date birthdate = Util.generateDate();
    int charmHelperId = RND.plusInt(charmHelperList.size());

    clientTestDao.get().insertClient(id, surname, name, patronymic, gender.name(), birthdate,
      charmHelperList.get(charmHelperId).id);

    return clientRecordBuilder(id, Util.getFullname(surname, name, patronymic), Util.getAge(birthdate),
      charmHelperList.get(charmHelperId).name);
  }

  public long insertClient(String dummyName, List<CharmHelper> charmHelperList) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().insertClient(id, dummyName, dummyName, dummyName,
      Gender.values()[RND.plusInt(Gender.values().length)].name(), Util.generateDate(),
      charmHelperList.get(RND.plusInt(charmHelperList.size())).id);
    return id;
  }

  public long insertClient(String surname, String name, String patronymic, String gender, LocalDate date, int charmId) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().insertClient(id, surname, name, patronymic, gender, Date.valueOf(date), charmId);

    return id;
  }

  public ClientRecord insertClient(String surname, String name, String patronymic, String gender, LocalDate date,
                                   int charmHelperId, List<CharmHelper> charmHelperList) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().insertClient(id, surname, name, patronymic, gender, Date.valueOf(date),
      charmHelperList.get(charmHelperId).id);

    return clientRecordBuilder(id, Util.getFullname(surname, name, patronymic), Util.getAge(date),
      charmHelperList.get(charmHelperId).name);
  }

  public long insertClient(String surname, String name, String patronymic, String gender, Date date, int charmId) {
    long id = clientTestDao.get().selectSeqIdNextValueTableClient();
    clientTestDao.get().insertClient(id, surname, name, patronymic, gender, date, charmId);
    return id;
  }

  public void updateClient(long id, String surname, String name, String patronymic, String gender, Date date,
                           int charmId) {
    clientTestDao.get().updateClient(id, surname, name, patronymic, gender, date, charmId);
  }

  public Phone phoneBuilder(String number, PhoneType type) {
    Phone phone = new Phone();
    phone.number = number;
    phone.type = type;

    return phone;
  }

  public ClientRecord clientRecordBuilder(long id, String fullname, int age, String charmName) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = id;
    clientRecord.age = age;
    clientRecord.fullName = fullname;
    clientRecord.charmName = charmName;
    clientRecord.totalAccountBalance = Util.floatToString((float) RND.plusDouble(100000, Util.decimalNum) - 50000);
    clientRecord.totalAccountBalance = Util.floatToString((float) RND.plusDouble(100000, Util.decimalNum) - 50000);
    clientRecord.totalAccountBalance = Util.floatToString((float) RND.plusDouble(100000, Util.decimalNum) - 50000);

    return clientRecord;
  }

  public ClientRecordRequest clientRecordRequestBuilder(long clientRecordCountToSkip, long clientRecordCount,
                                                        ColumnSortType columnSortType, boolean sortAscend,
                                                        String nameFilter) {
    ClientRecordRequest clientRecordRequest = new ClientRecordRequest();
    clientRecordRequest.clientRecordCountToSkip = clientRecordCountToSkip;
    clientRecordRequest.clientRecordCount = clientRecordCount;
    clientRecordRequest.columnSortType = columnSortType;
    clientRecordRequest.sortAscend = sortAscend;
    clientRecordRequest.nameFilter = nameFilter;

    return clientRecordRequest;
  }

  public void resetClientTablesAll() {
    clientTestDao.get().deleteAllTableClientPhone();
    clientTestDao.get().deleteAllTableClient();
    clientTestDao.get().deleteAllTableCharm();
  }
}
