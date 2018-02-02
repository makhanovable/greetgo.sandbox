package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportViewXlsx;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientListReportTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReportRegisterTest extends ParentTestNg {

  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<ClientListReportTestDao> reportTestDao;
  public BeanGetter<ReportRegister> reportRegister;

  @Test
  public void method_save_exists() throws Exception {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);

    ClientRecordRequest request = new ClientRecordRequest();
    request.clientRecordCount = 0;
    request.clientRecordCountToSkip = 0;
    request.nameFilter = "";
    request.columnSortType = ColumnSortType.NONE;
    request.sortAscend = false;

    String expectedToken = reportRegister.get().save(id, request, FileContentType.PDF);

    assertThat(reportTestDao.get().selectTokenExists(expectedToken)).isEqualTo(true);
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_invalidToken() throws Exception {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);

    ClientRecordRequest request = new ClientRecordRequest();
    request.clientRecordCount = 0;
    request.clientRecordCountToSkip = 0;
    request.nameFilter = "";
    request.columnSortType = ColumnSortType.NONE;
    request.sortAscend = false;

    String token = reportRegister.get().save(id, request, FileContentType.PDF);

    reportRegister.get().checkForValidity(token + RND.str(3));
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_fields() throws Exception {
    this.resetTables();

    String expectedPersonId = RND.str(10);
    authTestDao.get().insertUser(expectedPersonId, RND.str(10), RND.str(10), RND.plusInt(1));

    ClientRecordRequest expectedRequest = new ClientRecordRequest();
    expectedRequest.clientRecordCount = RND.plusInt(100);
    expectedRequest.clientRecordCountToSkip = RND.plusInt(100);
    expectedRequest.nameFilter = RND.str(5);
    expectedRequest.columnSortType = ColumnSortType.values()[RND.plusInt(ColumnSortType.values().length)];
    expectedRequest.sortAscend = RND.bool();
    FileContentType expectedFileType = FileContentType.values()[RND.plusInt(FileContentType.values().length)];

    String expectedReportIdInstance = reportRegister.get().save(expectedPersonId, expectedRequest, expectedFileType);

    ClientListReportInstance realReportInstance = reportRegister.get().checkForValidity(expectedPersonId);
    ClientRecordRequest realRequest = ClientRecordRequest.deserialize(realReportInstance.request);

    assertThat(realReportInstance.reportIdInstance).isEqualTo(expectedReportIdInstance);
    assertThat(realReportInstance.personId).isEqualTo(expectedPersonId);
    assertThat(realRequest.clientRecordCount).isEqualTo(expectedRequest.clientRecordCount);
    assertThat(realRequest.clientRecordCountToSkip).isEqualTo(expectedRequest.clientRecordCountToSkip);
    assertThat(realRequest.nameFilter).isEqualTo(expectedRequest.nameFilter);
    assertThat(realRequest.columnSortType).isEqualTo(expectedRequest.columnSortType);
    assertThat(realRequest.sortAscend).isEqualTo(expectedRequest.sortAscend);
    assertThat(FileContentType.valueOf(realReportInstance.fileTypeName)).isEqualTo(expectedFileType);
  }

  @Test
  public void method_generate_ok_noCheck() {
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

    OutputStream outputStream = new ByteArrayOutputStream();
    ClientRecordRequest expectedRequest = new ClientRecordRequest();
    expectedRequest.clientRecordCount = RND.plusInt(100);
    expectedRequest.clientRecordCountToSkip = RND.plusInt(100);
    expectedRequest.nameFilter = RND.str(5);
    expectedRequest.columnSortType = ColumnSortType.values()[RND.plusInt(ColumnSortType.values().length)];
    expectedRequest.sortAscend = RND.bool();
    FileContentType expectedFileType = FileContentType.values()[RND.plusInt(FileContentType.values().length)];

    reportRegister.get().generate(new ClientListReportViewXlsx(outputStream), expectedPersonDot.id, expectedRequest);
  }

  private void resetTables() {
    authTestDao.get().deleteAllTablePerson();
    reportTestDao.get().deleteAll();
  }
}
