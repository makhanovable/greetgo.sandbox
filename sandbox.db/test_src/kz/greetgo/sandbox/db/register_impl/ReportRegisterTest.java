package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientListReportTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReportRegisterTest extends ParentTestNg {

  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<ClientListReportTestDao> reportTestDao;
  public BeanGetter<ReportRegister> reportRegister;

  @Test
  public void method_save_exists() throws Exception {
    this.resetTables();

    String expectedPersonId = RND.str(10);
    authTestDao.get().insertUser(expectedPersonId, "account", "1234", 0);

    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.personId = expectedPersonId;
    reportInstance.request = new ClientRecordRequest();
    reportInstance.request.clientRecordCount = 0;
    reportInstance.request.clientRecordCountToSkip = 0;
    reportInstance.request.nameFilter = "";
    reportInstance.request.columnSortType = ColumnSortType.NONE;
    reportInstance.request.sortAscend = false;
    reportInstance.fileTypeName = FileContentType.PDF.name();
    String expectedToken = reportRegister.get().saveClientListReportInstance(reportInstance);

    assertThat(reportTestDao.get().selectTokenExists(expectedToken)).isEqualTo(true);
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_fields() throws Exception {
    this.resetTables();

    String expectedPersonId = RND.str(10);
    authTestDao.get().insertUser(expectedPersonId, "account", "1234", 0);

    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.personId = expectedPersonId;
    reportInstance.request = new ClientRecordRequest();
    reportInstance.request.clientRecordCount = 0;
    reportInstance.request.clientRecordCountToSkip = 0;
    reportInstance.request.nameFilter = "";
    reportInstance.request.columnSortType = ColumnSortType.NONE;
    reportInstance.request.sortAscend = false;
    reportInstance.fileTypeName = FileContentType.PDF.name();
    String expectedToken = reportRegister.get().saveClientListReportInstance(reportInstance);

    ClientListReportInstance realReportInstance = reportRegister.get().getClientListReportInstance(expectedPersonId);
    //TODO: deserialize in batis?
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
  public void method_generate_ok_noCheck() throws Exception {
    this.resetTables();

    PersonDot expectedPersonDot = new PersonDot();
    expectedPersonDot.id = RND.str(10);
    expectedPersonDot.accountName = RND.str(10);
    expectedPersonDot.name = RND.str(10);
    expectedPersonDot.surname = RND.str(10);
    expectedPersonDot.patronymic = RND.str(10);
    expectedPersonDot.disabled = false;
    expectedPersonDot.encryptedPassword = RND.str(10);
    expectedPersonDot.password = RND.str(10);

    authTestDao.get().insertPersonDot(expectedPersonDot);

    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.personId = expectedPersonDot.id;
    reportInstance.request = new ClientRecordRequest();
    reportInstance.request.clientRecordCount = 0;
    reportInstance.request.clientRecordCountToSkip = 0;
    reportInstance.request.nameFilter = "";
    reportInstance.request.columnSortType = ColumnSortType.NONE;
    reportInstance.request.sortAscend = false;
    reportInstance.fileTypeName = FileContentType.PDF.name();

    ClientListReportInstance realReportInstance = reportRegister.get()
      .getClientListReportInstance(expectedPersonDot.id);

    ClientListReportViewFake view = new ClientListReportViewFake();

    reportRegister.get().generateClientListReport(reportInstance, view);

    assertThat(view.headerData.columnSortType).isEqualTo(ColumnSortType.AGE);
    assertThat(view.list).hasSize(4);
    assertThat(view.list.get(0).age).isEqualTo(123);
  }

  private void resetTables() {
    authTestDao.get().deleteAllTablePerson();
    reportTestDao.get().deleteAll();
  }
}
