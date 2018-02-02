package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.ClientListReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.db.register_impl.report.client_list.ClientListReportViewPdf;
import kz.greetgo.sandbox.db.register_impl.report.client_list.ClientListReportViewXlsx;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.dao.AuthDao;
import kz.greetgo.sandbox.db.dao.ClientListReportDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.client_list.GetClientListReport;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.OutputStream;

@Bean
public class ClientListReportRegisterImpl implements ClientListReportRegister {

  public BeanGetter<AuthDao> authDao;
  public BeanGetter<ClientListReportDao> clientListReportDao;
  public BeanGetter<JdbcSandbox> jdbc;

  private static final int REPORT_ID_LENGTH = 16;

  @Override
  public String save(String personId, ClientRecordRequest request, FileContentType fileContentType) throws Exception {
    String reportIdInstance = Util.generateRandomString(REPORT_ID_LENGTH);

    clientListReportDao.get().insert(reportIdInstance, personId, ClientRecordRequest.serialize(request),
      fileContentType.name());

    return reportIdInstance;
  }

  @Override
  public ClientListReportInstance checkForValidity(String reportIdInstance) {
    ClientListReportInstance clientListReportInstance =
      clientListReportDao.get().selectInstance(reportIdInstance);

    if (clientListReportInstance == null) throw new AuthError("Invalid report id");

    clientListReportDao.get().delete(reportIdInstance);

    return clientListReportInstance;
  }

  @Override
  public void generate(OutputStream outputStream, String personId, ClientRecordRequest request, FileContentType
    fileContentType) {
    UserInfo userInfo = authDao.get().getUserInfo(personId);
    ClientListReportView reportView;

    switch (fileContentType) {
      case PDF:
        reportView = new ClientListReportViewPdf();
        break;
      default:
        reportView = new ClientListReportViewXlsx();
        break;
    }

    jdbc.get().execute(new GetClientListReport(outputStream, request,
      Util.getFullname(userInfo.surname, userInfo.name, userInfo.patronymic), reportView));
  }

}
