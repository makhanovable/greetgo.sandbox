package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.util.Util;
import org.testng.annotations.Test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ClientCommonTest {

  @Test
  public void method_getCount_filterEmpty() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedClientCount = 40;
    for (int i = 0; i < expectedClientCount; i++)
      this.insertClient(charmHelperList);

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(0, 0, ColumnSortType.NONE, false, "");

    long realCount = clientRegister.get().getCount(clientRecordRequest);

    assertThat(realCount).isEqualTo(expectedClientCount);
  }

  @Test
  public void method_getCount_filter() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    this.insertClient("Исаков", "Владимир", "Вячеславович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);
    expectedIdSet.add(this.insertClient("Яковлева", "Нургиза", "Андреевна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Яковлева", "Татьяна", "Нурлановна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Игорев", "Айдану", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(0, 0, ColumnSortType.NONE, false, "Нур");

    long realFilteredCount = clientRegister.get().getCount(clientRecordRequest);

    assertThat(realFilteredCount).isEqualTo(expectedIdSet.size());
  }

  @Test
  public void method_getCount_filterWithIgnoredCase() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Исаков", "Владимир", "Вячеславович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("АйланУр", "Лалка", "Иванов", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Яковлева", "Гизанур", "Андреевна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Яковлева", "Татьяна", "Нурлановна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(0, 0, ColumnSortType.NONE, false, "нУР");

    long realFilteredCount = clientRegister.get().getCount(clientRecordRequest);

    assertThat(realFilteredCount).isEqualTo(expectedIdSet.size());
  }

  @Test
  public void method_getRecordList_default() {
    this.resetClientTablesAll();

    ClientRecordSetHelper clientRecordSetHelper = getRecordList_default();
    Set<Long> expectedIdSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.id).collect(Collectors.toSet());

    List<ClientRecord> realRecordList =
      clientRegister.get().getRecordList(clientRecordSetHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(clientRecordSetHelper.clientRecordSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atBeginning() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    expectedIdSet.add(this.insertClient("Нурбакыт", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("а", "б", "в", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Игорев", "Игорь", "Игоревич", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 10; i++)
      this.insertClient(charmHelperList);

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(0, expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atMiddle() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList).id);
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 10; i++)
      this.insertClient(charmHelperList);

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(skippedIdSet.size(), expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atEnd() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList).id);
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(skippedIdSet.size(), expectedIdSet.size(), ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_onCut() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 10; i++)
      skippedIdSet.add(this.insertClient(charmHelperList).id);
    expectedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest = this.clientRecordRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size() + 2, ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList)
      assertThat(clientRecord.id).isIn(expectedIdSet);
  }

  @Test
  public void method_getRecordList_defaultWithPagination_atCountExceed() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> skippedIdSet = new HashSet<>();
    for (int i = 0; i < 12; i++)
      skippedIdSet.add(this.insertClient(charmHelperList).id);
    skippedIdSet.add(this.insertClient("ПУСТО", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("Нурланов", "Нурлан", "Нурлы", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(skippedIdSet.size(), 10, ColumnSortType.NONE, false, "");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList).isEmpty();
  }

  @Test
  public void method_getRecordList_sortAgeAscend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortAgeAscend();
    List<Integer> expectedAgeList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.age).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedAgeList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).age).isEqualTo(expectedAgeList.get(i));
  }

  @Test
  public void method_getRecordList_sortAgeDescend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortAgeDescend();
    List<Integer> expectedAgeList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.age).collect(Collectors.toList());

    List<ClientRecord> realRecordList =
      clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedAgeList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).age).isEqualTo(expectedAgeList.get(i));
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceAscend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortTotalAccountBalanceAscend();
    List<String> expectedTotalMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.totalAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedTotalMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).totalAccountBalance).isEqualTo(expectedTotalMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceDescend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortTotalAccountBalanceDescend();
    List<String> expectedTotalMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.totalAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedTotalMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(realRecordList.get(i).totalAccountBalance).isEqualTo(expectedTotalMoneyList.get(i));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceAscend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMaxAccountBalanceAscend();
    List<String> expectedMaxMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.maxAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMaxMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Float.parseFloat(realRecordList.get(i).maxAccountBalance))
        .isEqualTo(Float.parseFloat(expectedMaxMoneyList.get(i)));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceDescend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMaxAccountBalanceDescend();
    List<String> expectedMaxMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.maxAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedMaxMoneyList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Float.parseFloat(realRecordList.get(i).maxAccountBalance))
        .isEqualTo(Float.parseFloat(expectedMaxMoneyList.get(i)));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceAscend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMinAccountBalanceAscend();
    List<String> expectedMinMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.minAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(clientRecordListHelper.clientRecordList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Float.parseFloat(realRecordList.get(i).minAccountBalance))
        .isEqualTo(Float.parseFloat(expectedMinMoneyList.get(i)));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceDescend() {
    this.resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMinAccountBalanceDescend();
    List<String> expectedMinMoneyList = clientRecordListHelper.clientRecordList.stream()
      .map(clientRecord -> clientRecord.minAccountBalance).collect(Collectors.toList());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordListHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(clientRecordListHelper.clientRecordList.size());
    for (int i = 0; i < realRecordList.size(); i++)
      assertThat(Float.parseFloat(realRecordList.get(i).minAccountBalance))
        .isEqualTo(Float.parseFloat(expectedMinMoneyList.get(i)));
  }

  @Test
  public void method_getRecordList_filter() {
    this.resetClientTablesAll();

    ClientRecordSetHelper clientRecordSetHelper = getRecordList_filter();
    Set<Long> expectedIdSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.id).collect(Collectors.toSet());
    Set<String> expectedFullnameSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.fullName).collect(Collectors.toSet());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordSetHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(clientRecordSetHelper.clientRecordSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName).isIn(expectedFullnameSet);
    }
  }

  @Test
  public void method_getRecordList_filterOnEmptyName() {
    this.resetClientTablesAll();

    ClientRecordSetHelper clientRecordSetHelper = getRecordList_filterOnEmptyName();
    Set<Long> expectedIdSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.id).collect(Collectors.toSet());
    Set<String> expectedFullnameSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.fullName).collect(Collectors.toSet());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordSetHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName).isIn(expectedFullnameSet);
    }
  }

  @Test
  public void method_getRecordList_filterWithIgnoredCase() {
    this.resetClientTablesAll();

    ClientRecordSetHelper clientRecordSetHelper = getRecordList_filterWithIgnoredCase();
    Set<Long> expectedIdSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.id).collect(Collectors.toSet());
    Set<String> expectedFullnameSet = clientRecordSetHelper.clientRecordSet.stream()
      .map(clientRecord -> clientRecord.fullName).collect(Collectors.toSet());

    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordSetHelper.clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName).isIn(expectedFullnameSet);
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atBeginning() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Айнур", "Айбек", "Смагулович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("а", "Нургиза", "в", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 3; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Игорев", "Игорь", "Нурик", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("Байконур", "Игорь", "Вячеслав", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(0, expectedIdSet.size(), ColumnSortType.NONE, false, "нур");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("Айнур Айбек Смагулович", "а Нургиза в", "Игорев Игорь Нурик", "Байконур Игорь Вячеслав");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atMiddle() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    skippedIdSet.add(this.insertClient("айбек", "Айбек", "айбековна", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Айбек", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("ч", "тнур", "Айбек", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    expectedIdSet.add(this.insertClient("Нурланов", "Айбек", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    this.insertClient("айбек", "нулл", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id);

    ClientRecordRequest clientRecordRequest = this.clientRecordRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size(), ColumnSortType.NONE, false, "айбек");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("ч тнур Айбек", "Нурланов Айбек Нурланович");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atEnd() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("кваулы", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "кваулификация", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("ч", "Квауентин", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "фвф", "Кваута", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("кваукер", "Нурлан", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "квауква", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest = this.clientRecordRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size(), ColumnSortType.NONE, false, "квау");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName)
        .isIn("кваукер Нурлан Нурланович", "Нурланов квауква Нурланович");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_onCut() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    Set<Long> expectedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("ПУСТО", "ПУСТОп", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("стоп", "т", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Нурланов", "стопльник", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("стопка", "кваква", "Нурланович", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    expectedIdSet.add(this.insertClient("Нурланов", "кваква", "СТОп", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);

    ClientRecordRequest clientRecordRequest = this.clientRecordRequestBuilder(skippedIdSet.size(),
      expectedIdSet.size() + 10, ColumnSortType.NONE, false, "стоп");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList.size()).isEqualTo(expectedIdSet.size());
    for (ClientRecord clientRecord : realRecordList) {
      assertThat(clientRecord.id).isIn(expectedIdSet);
      assertThat(clientRecord.fullName).isIn("стопка кваква Нурланович", "Нурланов кваква СТОп");
    }
  }

  @Test
  public void method_getRecordList_filterWithPagination_atCountExceed() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    String dummyName = "null";
    Set<Long> skippedIdSet = new HashSet<>();
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("Боборис", "ПУСТО", "ПУСТО", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("ч", "бобош", "о", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    for (int i = 0; i < 4; i++)
      this.insertClient(dummyName, charmHelperList);
    skippedIdSet.add(this.insertClient("бобоевик", "Нурлан", "Нурлы", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));
    skippedIdSet.add(this.insertClient("null", "Нурлан", "Бобово", Gender.EMPTY.name(), LocalDate.now(),
      charmHelperList.get(0).id));

    ClientRecordRequest clientRecordRequest =
      this.clientRecordRequestBuilder(skippedIdSet.size() + 5, 10, ColumnSortType.NONE, false, "бобо");
    List<ClientRecord> realRecordList = clientRegister.get().getRecordList(clientRecordRequest);

    assertThat(realRecordList).isEmpty();
  }

  @Test
  public void method_removeDetails_default_single() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    Long removingId;
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList).id);
    removingId = this.insertClient(charmHelperList).id;
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList).id);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size() + 1);
    assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(true);

    clientRegister.get().removeRecord(removingId);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size());
    assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(false);
  }

  @Test
  public void method_removeDetails_default_several() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    Set<Long> expectedIdSet = new HashSet<>();
    List<Long> removingIdSet = new ArrayList<>();
    removingIdSet.add(this.insertClient(charmHelperList).id);
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList).id);
    removingIdSet.add(this.insertClient(charmHelperList).id);
    for (int i = 0; i < 4; i++)
      expectedIdSet.add(this.insertClient(charmHelperList).id);
    removingIdSet.add(this.insertClient(charmHelperList).id);
    removingIdSet.add(this.insertClient(charmHelperList).id);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size() + removingIdSet.size());
    for (Long removingId : removingIdSet)
      assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(true);

    for (Long removingId : removingIdSet)
      clientRegister.get().removeRecord(removingId);

    assertThat(clientTestDao.get().selectCountTableClient()).isEqualTo(expectedIdSet.size());
    for (Long removingId : removingIdSet)
      assertThat(clientTestDao.get().selectExistSingleTableClient(removingId)).isEqualTo(false);
  }

  @Test
  public void method_getDetails_addOperation() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    int charmIdToSkip = charmHelperList.get(2).id;
    clientTestDao.get().updateDisableSingleTableCharm(charmIdToSkip);
    Set<Integer> expectedCharmIdSet =
      charmHelperList.stream().map(ch -> ch.id).filter(id -> id != charmIdToSkip).collect(Collectors.toSet());

    ClientDetails realClientDetails = clientRegister.get().getDetails(null);
    Set<Integer> realCharmIdSet =
      realClientDetails.charmList.stream().map(c -> c.id).collect(Collectors.toSet());

    assertThat(realClientDetails.id).isNull();
    assertThat(realClientDetails.surname).isEmpty();
    assertThat(realClientDetails.name).isEmpty();
    assertThat(realClientDetails.patronymic).isEmpty();
    assertThat(realClientDetails.gender.name()).isEqualTo(Gender.EMPTY.name());
    assertThat(realClientDetails.birthdate).isEmpty();
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(0).id);
    assertThat(realCharmIdSet.size()).isEqualTo(expectedCharmIdSet.size());
    for (Integer realCharmId : realCharmIdSet)
      assertThat(realCharmId).isIn(expectedCharmIdSet);
    assertThat(realClientDetails.registrationAddressInfo.type).isEqualTo(AddressType.REGISTRATION);
    assertThat(realClientDetails.registrationAddressInfo.street).isEmpty();
    assertThat(realClientDetails.registrationAddressInfo.house).isEmpty();
    assertThat(realClientDetails.registrationAddressInfo.flat).isEmpty();
    assertThat(realClientDetails.factualAddressInfo.type).isEqualTo(AddressType.FACTUAL);
    assertThat(realClientDetails.factualAddressInfo.street).isEmpty();
    assertThat(realClientDetails.factualAddressInfo.house).isEmpty();
    assertThat(realClientDetails.factualAddressInfo.flat).isEmpty();
    assertThat(realClientDetails.phones).isEmpty();
  }

  @Test
  public void method_getDetails_editOperation() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    clientTestDao.get().updateDisableSingleTableCharm(charmHelperList.get(3).id);
    Set<Integer> expectedCharmIdSet =
      charmHelperList.stream().map(ch -> ch.id).filter(id -> id != charmHelperList.get(3).id).collect(Collectors.toSet());
    long expectedId;
    String dummyName = "test";
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = this.insertClient(dummyName, dummyName, dummyName, Gender.EMPTY.name(),
      Date.valueOf(LocalDate.ofEpochDay(0)), charmHelperList.get(0).id);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);

    this.insertClientAddress(expectedId, AddressType.REGISTRATION, "Шевченко", "45а", "3");
    this.insertClientAddress(expectedId, AddressType.FACTUAL, "Абай", "21б", "52");

    this.insertClientPhone(expectedId, "+72822590121", PhoneType.HOME);
    this.insertClientPhone(expectedId, "+77071112233", PhoneType.MOBILE);
    this.insertClientPhone(expectedId, "+77471234567", PhoneType.MOBILE);
    this.insertClientPhone(expectedId, "+77770001155", PhoneType.OTHER);

    Date expectedDate = Date.valueOf(LocalDate.now());
    this.updateClient(expectedId, "Фамилия", "Имя", "Отчество", Gender.MALE.name(), expectedDate,
      charmHelperList.get(1).id);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);

    ClientDetails realClientDetails = clientRegister.get().getDetails(expectedId);
    Set<Integer> realCharmIdSet =
      realClientDetails.charmList.stream().map(c -> c.id).collect(Collectors.toSet());

    assertThat(realClientDetails.id).isEqualTo(expectedId);
    assertThat(realClientDetails.surname).isEqualTo("Фамилия");
    assertThat(realClientDetails.name).isEqualTo("Имя");
    assertThat(realClientDetails.patronymic).isEqualTo("Отчество");
    assertThat(realClientDetails.gender).isEqualTo(Gender.MALE);
    assertThat(realClientDetails.birthdate).isEqualTo(new SimpleDateFormat(Util.datePattern).format(expectedDate));
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(1).id);
    assertThat(realCharmIdSet.size()).isEqualTo(expectedCharmIdSet.size());
    for (Integer realCharmId : realCharmIdSet)
      assertThat(realCharmId).isIn(expectedCharmIdSet);
    assertThat(realClientDetails.registrationAddressInfo.type).isEqualTo(AddressType.REGISTRATION);
    assertThat(realClientDetails.registrationAddressInfo.street).isEqualTo("Шевченко");
    assertThat(realClientDetails.registrationAddressInfo.house).isEqualTo("45а");
    assertThat(realClientDetails.registrationAddressInfo.flat).isEqualTo("3");
    assertThat(realClientDetails.factualAddressInfo.type).isEqualTo(AddressType.FACTUAL);
    assertThat(realClientDetails.factualAddressInfo.street).isEqualTo("Абай");
    assertThat(realClientDetails.factualAddressInfo.house).isEqualTo("21б");
    assertThat(realClientDetails.factualAddressInfo.flat).isEqualTo("52");
    assertThat(realClientDetails.phones).hasSize(4);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+72822590121".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+77071112233".equals(phone.number) && phone.type == PhoneType.MOBILE)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+77471234567".equals(phone.number) && phone.type == PhoneType.MOBILE)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+77770001155".equals(phone.number) && phone.type == PhoneType.OTHER)).isEqualTo(true);
  }

  @Test
  public void method_saveDetails_addOperation() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedId;
    Date expectedDate = Date.valueOf(LocalDate.now());

    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = this.insertClient(charmHelperList).id + 1;

    ClientDetailsToSave expectedClientDetailsToSave = new ClientDetailsToSave();
    expectedClientDetailsToSave.id = null;
    expectedClientDetailsToSave.surname = "surname";
    expectedClientDetailsToSave.name = "lastname";
    expectedClientDetailsToSave.patronymic = "patronymic";
    expectedClientDetailsToSave.gender = Gender.FEMALE;
    expectedClientDetailsToSave.birthdate = expectedDate.toString();
    expectedClientDetailsToSave.charmId = charmHelperList.get(3).id;
    expectedClientDetailsToSave.registrationAddressInfo = new AddressInfo();
    expectedClientDetailsToSave.registrationAddressInfo.type = AddressType.REGISTRATION;
    expectedClientDetailsToSave.registrationAddressInfo.street = "street";
    expectedClientDetailsToSave.registrationAddressInfo.house = "home";
    expectedClientDetailsToSave.registrationAddressInfo.flat = "flat";
    expectedClientDetailsToSave.factualAddressInfo = new AddressInfo();
    expectedClientDetailsToSave.factualAddressInfo.type = AddressType.FACTUAL;
    expectedClientDetailsToSave.factualAddressInfo.street = "street-res";
    expectedClientDetailsToSave.factualAddressInfo.house = "home-res";
    expectedClientDetailsToSave.factualAddressInfo.flat = "flat-res";
    expectedClientDetailsToSave.phones = new ArrayList<>();
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+71111", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+77071230011", PhoneType.EMBEDDED));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+70000", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+00000", PhoneType.OTHER));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("111111", PhoneType.WORK));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("222222", PhoneType.OTHER));

    ClientRecord realClientRecord = clientRegister.get().save(expectedClientDetailsToSave);
    ClientDetails realClientDetails = clientTestDao.get().selectRowById(expectedId);
    realClientDetails.factualAddressInfo =
      clientTestDao.get().selectRowByClientAndTypeTableClientAddr(expectedId, AddressType.FACTUAL.name());
    realClientDetails.registrationAddressInfo =
      clientTestDao.get().selectRowByClientAndTypeTableClientAddr(expectedId, AddressType.REGISTRATION.name());
    realClientDetails.phones =
      clientTestDao.get().selectRowsByClientTableClientPhone(expectedId);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);
    assertThat(realClientDetails.surname).isEqualTo("surname");
    assertThat(realClientDetails.name).isEqualTo("lastname");
    assertThat(realClientDetails.patronymic).isEqualTo("patronymic");
    assertThat(realClientDetails.gender).isEqualTo(Gender.FEMALE);
    assertThat(realClientDetails.birthdate).isEqualTo(new SimpleDateFormat(Util.datePattern).format(expectedDate));
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(3).id);
    assertThat(realClientDetails.registrationAddressInfo.type).isEqualTo(AddressType.REGISTRATION);
    assertThat(realClientDetails.registrationAddressInfo.street).isEqualTo("street");
    assertThat(realClientDetails.registrationAddressInfo.house).isEqualTo("home");
    assertThat(realClientDetails.registrationAddressInfo.flat).isEqualTo("flat");
    assertThat(realClientDetails.factualAddressInfo.type).isEqualTo(AddressType.FACTUAL);
    assertThat(realClientDetails.factualAddressInfo.street).isEqualTo("street-res");
    assertThat(realClientDetails.factualAddressInfo.house).isEqualTo("home-res");
    assertThat(realClientDetails.factualAddressInfo.flat).isEqualTo("flat-res");
    assertThat(realClientDetails.phones).hasSize(6);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+71111".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+77071230011".equals(phone.number) && phone.type == PhoneType.EMBEDDED)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+70000".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+00000".equals(phone.number) && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "111111".equals(phone.number) && phone.type == PhoneType.WORK)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "222222".equals(phone.number) && phone.type == PhoneType.OTHER)).isEqualTo(true);

    assertThat(realClientRecord).isNotNull();
    assertThat(realClientRecord.fullName).isEqualTo("surname lastname patronymic");
    assertThat(realClientRecord.age).isEqualTo(Util.getAge(expectedDate));
    assertThat(realClientRecord.charmName).isEqualTo(charmHelperList.get(3).name);
    assertThat(Float.parseFloat(realClientRecord.totalAccountBalance)).isEqualTo(0);
    assertThat(Float.parseFloat(realClientRecord.maxAccountBalance)).isEqualTo(0);
    assertThat(Float.parseFloat(realClientRecord.minAccountBalance)).isEqualTo(0);
  }

  @Test
  public void method_saveDetails_editOperation() {
    this.resetClientTablesAll();
    List<CharmHelper> charmHelperList = this.declareAndInsertCharms();

    long expectedId;
    Date expectedDate = Date.valueOf(LocalDate.now());
    String dummyName = "test";
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);
    expectedId = this.insertClient(dummyName, dummyName, dummyName, Gender.EMPTY.name(),
      Date.valueOf(LocalDate.ofEpochDay(0)), charmHelperList.get(0).id);
    for (int i = 0; i < 4; i++)
      this.insertClient(charmHelperList);

    this.insertClientAddress(expectedId, AddressType.REGISTRATION, "Шевченко", "45а", "3");
    this.insertClientAddress(expectedId, AddressType.FACTUAL, "", "", "");

    this.insertClientPhone(expectedId, "+72822590121", PhoneType.HOME);
    this.insertClientPhone(expectedId, "111111", PhoneType.OTHER);

    ClientDetailsToSave expectedClientDetailsToSave = new ClientDetailsToSave();
    expectedClientDetailsToSave.id = expectedId;
    expectedClientDetailsToSave.surname = "surname";
    expectedClientDetailsToSave.name = "lastname";
    expectedClientDetailsToSave.patronymic = "patronymic";
    expectedClientDetailsToSave.gender = Gender.MALE;
    expectedClientDetailsToSave.birthdate = expectedDate.toString();
    expectedClientDetailsToSave.charmId = charmHelperList.get(1).id;
    expectedClientDetailsToSave.registrationAddressInfo = new AddressInfo();
    expectedClientDetailsToSave.registrationAddressInfo.type = AddressType.REGISTRATION;
    expectedClientDetailsToSave.registrationAddressInfo.street = "street";
    expectedClientDetailsToSave.registrationAddressInfo.house = "home";
    expectedClientDetailsToSave.registrationAddressInfo.flat = "flat";
    expectedClientDetailsToSave.factualAddressInfo = new AddressInfo();
    expectedClientDetailsToSave.factualAddressInfo.type = AddressType.FACTUAL;
    expectedClientDetailsToSave.factualAddressInfo.street = "street-res";
    expectedClientDetailsToSave.factualAddressInfo.house = "home-res";
    expectedClientDetailsToSave.factualAddressInfo.flat = "flat-res";
    expectedClientDetailsToSave.phones = new ArrayList<>();
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+71111", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+77071230011", PhoneType.EMBEDDED));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+70000", PhoneType.HOME));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("+00000", PhoneType.OTHER));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("111111", PhoneType.WORK));
    expectedClientDetailsToSave.phones.add(this.phoneBuilder("222222", PhoneType.OTHER));

    ClientRecord realClientRecord = clientRegister.get().save(expectedClientDetailsToSave);

    ClientDetails realClientDetails = clientTestDao.get().selectRowById(expectedId);
    realClientDetails.factualAddressInfo =
      clientTestDao.get().selectRowByClientAndTypeTableClientAddr(expectedId, AddressType.FACTUAL.name());
    realClientDetails.registrationAddressInfo =
      clientTestDao.get().selectRowByClientAndTypeTableClientAddr(expectedId, AddressType.REGISTRATION.name());
    realClientDetails.phones =
      clientTestDao.get().selectRowsByClientTableClientPhone(expectedId);
    clientTestDao.get().selectRowsByClientTableClientPhone(expectedId);

    assertThat(clientTestDao.get().selectExistSingleTableClient(expectedId)).isEqualTo(true);
    assertThat(realClientDetails.id).isEqualTo(expectedId);
    assertThat(realClientDetails.surname).isEqualTo("surname");
    assertThat(realClientDetails.name).isEqualTo("lastname");
    assertThat(realClientDetails.patronymic).isEqualTo("patronymic");
    assertThat(realClientDetails.gender.name()).isEqualTo(Gender.MALE.name());
    assertThat(realClientDetails.birthdate).isEqualTo(new SimpleDateFormat(Util.datePattern).format(expectedDate));
    assertThat(realClientDetails.charmId).isEqualTo(charmHelperList.get(1).id);
    assertThat(realClientDetails.registrationAddressInfo.type).isEqualTo(AddressType.REGISTRATION);
    assertThat(realClientDetails.registrationAddressInfo.street).isEqualTo("street");
    assertThat(realClientDetails.registrationAddressInfo.house).isEqualTo("home");
    assertThat(realClientDetails.registrationAddressInfo.flat).isEqualTo("flat");
    assertThat(realClientDetails.factualAddressInfo.type).isEqualTo(AddressType.FACTUAL);
    assertThat(realClientDetails.factualAddressInfo.street).isEqualTo("street-res");
    assertThat(realClientDetails.factualAddressInfo.house).isEqualTo("home-res");
    assertThat(realClientDetails.factualAddressInfo.flat).isEqualTo("flat-res");
    assertThat(realClientDetails.phones).hasSize(7);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+71111".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+77071230011".equals(phone.number) && phone.type == PhoneType.EMBEDDED)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+70000".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+00000".equals(phone.number) && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "111111".equals(phone.number) && phone.type == PhoneType.WORK)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "222222".equals(phone.number) && phone.type == PhoneType.OTHER)).isEqualTo(true);
    assertThat(realClientDetails.phones.stream().anyMatch(phone ->
      "+72822590121".equals(phone.number) && phone.type == PhoneType.HOME)).isEqualTo(true);

    assertThat(realClientRecord).isNotNull();
    assertThat(realClientRecord.fullName).isEqualTo("surname lastname patronymic");
    assertThat(realClientRecord.age).isEqualTo(Util.getAge(expectedDate));
    assertThat(realClientRecord.charmName).isEqualTo(charmHelperList.get(1).name);
    assertThat(Float.parseFloat(realClientRecord.totalAccountBalance)).isEqualTo(0);
    assertThat(Float.parseFloat(realClientRecord.maxAccountBalance)).isEqualTo(0);
    assertThat(Float.parseFloat(realClientRecord.minAccountBalance)).isEqualTo(0);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getCount_requestNull() {
    clientRegister.get().getCount(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getCount_filterNameNull() {
    ClientRecordRequest clientRecordRequest =
      clientRecordRequestBuilder(-10, 0, ColumnSortType.NONE, false, null);

    clientRegister.get().getCount(clientRecordRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_requestNull() {
    clientRegister.get().getRecordList(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_filterNameNull() {
    ClientRecordRequest clientRecordRequest =
      clientRecordRequestBuilder(-10, 0, ColumnSortType.NONE, false, null);

    clientRegister.get().getRecordList(clientRecordRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countToSkipNegative() {
    ClientRecordRequest clientRecordRequest =
      clientRecordRequestBuilder(-10, 0, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countZero() {
    ClientRecordRequest clientRecordRequest =
      clientRecordRequestBuilder(0, 0, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getRecordList_countNegative() {
    ClientRecordRequest clientRecordRequest =
      clientRecordRequestBuilder(0, -10, ColumnSortType.NONE, false, "");

    clientRegister.get().getRecordList(clientRecordRequest);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_removeRecord_idNegative() {
    clientRegister.get().removeRecord(-100);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getDetails_idNegative() {
    clientRegister.get().getDetails(-10L);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_getDetails_idExists() {
    clientRegister.get().getDetails(9999999L);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_saveDetails_detailsNull() {
    clientRegister.get().save(null);
  }

  @Test(expectedExceptions = InvalidParameter.class)
  public void method_saveDetails_idExists() {
    ClientDetailsToSave clientDetailsToSave = new ClientDetailsToSave();
    clientDetailsToSave.id = 999999L;
    clientDetailsToSave.surname = "";
    clientDetailsToSave.name = "";
    clientDetailsToSave.patronymic = "";
    clientDetailsToSave.gender = Gender.EMPTY;
    clientDetailsToSave.birthdate = "";
    clientDetailsToSave.charmId = 0;

    clientRegister.get().save(clientDetailsToSave);
  }
}
