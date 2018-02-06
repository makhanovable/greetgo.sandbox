package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientListReportTestDao;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReportRegisterImplTest extends ClientCommonTest {

  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<ClientListReportTestDao> reportTestDao;
  public BeanGetter<ReportRegister> reportRegister;

  @Test
  public void method_save_exists() throws Exception {
    this.resetReportTablesAll();

    String expectedPersonId = RND.str(10);
    authTestDao.get().insertUser(expectedPersonId, "account", "1234", 0);

    ClientListReportInstance reportInstance = this.clientListReportInstanceBuilder(expectedPersonId,
      clientRecordRequestBuilder(0, 0, ColumnSortType.TOTALACCOUNTBALANCE, true, "test"));
    String expectedReportInstanceId = reportRegister.get().saveClientListReportInstance(reportInstance);

    assertThat(reportTestDao.get().selectReportInstanceIdExists(expectedReportInstanceId)).isEqualTo(true);
  }

  @Test
  public void method_checkSessionForValidity_fields() throws Exception {
    this.resetReportTablesAll();

    String expectedPersonId = RND.str(10);
    authTestDao.get().insertUser(expectedPersonId, "account", "1234", 0);

    ClientListReportInstance reportInstance = this.clientListReportInstanceBuilder(expectedPersonId,
      clientRecordRequestBuilder(0, 0, ColumnSortType.AGE, false, "hh"));
    String expectedReportInstanceId = reportRegister.get().saveClientListReportInstance(reportInstance);

    ClientListReportInstance realReportInstance =
      reportRegister.get().getClientListReportInstance(expectedReportInstanceId);
    ClientRecordRequest realRequest = realReportInstance.request;

    assertThat(realReportInstance.personId).isEqualTo(expectedPersonId);
    assertThat(realRequest.clientRecordCount).isEqualTo(reportInstance.request.clientRecordCount);
    assertThat(realRequest.clientRecordCountToSkip).isEqualTo(reportInstance.request.clientRecordCountToSkip);
    assertThat(realRequest.nameFilter).isEqualTo(reportInstance.request.nameFilter);
    assertThat(realRequest.columnSortType).isEqualTo(reportInstance.request.columnSortType);
    assertThat(realRequest.sortAscend).isEqualTo(reportInstance.request.sortAscend);
    assertThat(realReportInstance.fileTypeName).isEqualTo(reportInstance.fileTypeName);
  }

  @Test
  public void method_generate_default() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    this.testViewCommon(getRecordList_default());
  }

  @Test
  public void method_generate_sortAscend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortAgeAscend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).age).isEqualTo(clientRecordListHelper.clientRecordList.get(i).age);
  }

  @Test
  public void method_generate_sortDescend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortAgeDescend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).age).isEqualTo(clientRecordListHelper.clientRecordList.get(i).age);
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceAscend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortTotalAccountBalanceAscend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).totalAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).totalAccountBalance));
  }

  @Test
  public void method_getRecordList_sortTotalAccountBalanceDescend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortTotalAccountBalanceDescend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).totalAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).totalAccountBalance));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceAscend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMaxAccountBalanceAscend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).maxAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).maxAccountBalance));
  }

  @Test
  public void method_getRecordList_sortMaxAccountBalanceDescend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMaxAccountBalanceDescend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).maxAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).maxAccountBalance));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceAscend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMinAccountBalanceAscend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).minAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).minAccountBalance));
  }

  @Test
  public void method_getRecordList_sortMinAccountBalanceDescend() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    ClientRecordListHelper clientRecordListHelper = getRecordList_sortMinAccountBalanceDescend();
    ClientListReportViewFake fakeView = this.testViewCommon(clientRecordListHelper);

    for (int i = 0; i < fakeView.list.size(); i++)
      assertThat(fakeView.list.get(i).minAccountBalance)
        .isEqualTo(Util.stringToFloat(clientRecordListHelper.clientRecordList.get(i).minAccountBalance));
  }

  @Test
  public void method_generate_filter() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    this.testViewCommon(getRecordList_filter());
  }

  @Test
  public void method_getRecordList_filterOnEmptyName() throws Exception {
    this.resetReportTablesAll();
    resetClientTablesAll();

    this.testViewCommon(getRecordList_filterOnEmptyName());
  }

  private void testViewCommon(ClientRecordSetHelper clientRecordSetHelper) throws Exception {
    PersonDot expectedPersonDot = this.generatePersonDot();
    authTestDao.get().insertPersonDot(expectedPersonDot);

    ClientListReportInstance reportInstance =
      this.clientListReportInstanceBuilder(expectedPersonDot.id, clientRecordSetHelper.clientRecordRequest);

    ClientListReportViewFake view = new ClientListReportViewFake();
    reportRegister.get().generateClientListReport(reportInstance, view);

    assertThat(view.headerData.columnSortType).isEqualTo(clientRecordSetHelper.clientRecordRequest.columnSortType);
    assertThat(view.list.size()).isEqualTo(clientRecordSetHelper.clientRecordSet.size());
    for (ReportItemData reportItemData : view.list) {
      assertThat(true).isEqualTo(clientRecordSetHelper.clientRecordSet.stream()
        .anyMatch(clientRecord -> clientRecord.fullName.equals(reportItemData.fullname)));
      assertThat(true).isEqualTo(clientRecordSetHelper.clientRecordSet.stream()
        .anyMatch(clientRecord -> clientRecord.charmName.equals(reportItemData.charmName)));
    }
    assertThat(view.footerData.createdBy)
      .isEqualTo(Util.getFullname(expectedPersonDot.surname, expectedPersonDot.name, expectedPersonDot.patronymic));
  }

  private ClientListReportViewFake testViewCommon(ClientRecordListHelper clientRecordListHelper) throws Exception {
    PersonDot expectedPersonDot = this.generatePersonDot();
    authTestDao.get().insertPersonDot(expectedPersonDot);

    ClientListReportInstance reportInstance =
      this.clientListReportInstanceBuilder(expectedPersonDot.id, clientRecordListHelper.clientRecordRequest);

    ClientListReportViewFake viewFake = new ClientListReportViewFake();
    reportRegister.get().generateClientListReport(reportInstance, viewFake);

    assertThat(viewFake.headerData.columnSortType).isEqualTo(clientRecordListHelper.clientRecordRequest.columnSortType);
    assertThat(viewFake.list.size()).isEqualTo(clientRecordListHelper.clientRecordList.size());
    assertThat(viewFake.footerData.createdBy)
      .isEqualTo(Util.getFullname(expectedPersonDot.surname, expectedPersonDot.name, expectedPersonDot.patronymic));

    return viewFake;
  }

  private ClientListReportInstance clientListReportInstanceBuilder(String personId, ClientRecordRequest request) {
    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.personId = personId;
    reportInstance.request = request;
    reportInstance.fileTypeName = FileContentType.values()[RND.plusInt(FileContentType.values().length)].name();

    return reportInstance;
  }

  private PersonDot generatePersonDot() {
    PersonDot personDot = new PersonDot();
    personDot.id = RND.str(10);
    personDot.accountName = RND.str(10);
    personDot.name = RND.str(10);
    personDot.surname = RND.str(10);
    personDot.patronymic = RND.str(10);
    personDot.disabled = false;
    personDot.encryptedPassword = RND.str(10);
    personDot.password = RND.str(10);

    return personDot;
  }

  private void resetReportTablesAll() {
    authTestDao.get().deleteAllTablePerson();
    reportTestDao.get().deleteAll();
  }
}
