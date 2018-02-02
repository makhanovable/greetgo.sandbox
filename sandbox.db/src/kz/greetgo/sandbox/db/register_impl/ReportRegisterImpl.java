package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.dao.AuthDao;
import kz.greetgo.sandbox.db.dao.ClientListReportDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.client_list.GetClientListReport;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Bean
public class ReportRegisterImpl implements ReportRegister {

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
  public void prepareForGeneration(RequestTunnel requestTunnel, String fileName, FileContentType fileContentType) {
    String contentType, fileType;
    switch (fileContentType) {
      case PDF:
        contentType = "application/pdf";
        fileType = "pdf";
        break;
      case XLSX:
        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        fileType = "xlsx";
        break;
      default:
        throw new InvalidParameter();
    }
    requestTunnel.setResponseContentType(contentType);
    requestTunnel.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)) + "." + fileType + "\"");
  }

  @Override
  public void generate(ClientListReportView reportView, String personId, ClientRecordRequest request) {
    UserInfo userInfo = authDao.get().getUserInfo(personId);

    jdbc.get().execute(new GetClientListReport(request,
      Util.getFullname(userInfo.surname, userInfo.name, userInfo.patronymic), reportView));
  }
}
