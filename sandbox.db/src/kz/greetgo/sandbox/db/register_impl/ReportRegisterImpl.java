package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.dao.AuthDao;
import kz.greetgo.sandbox.db.dao.ClientListReportDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.client_list.GetClientListReport;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

@Bean
public class ReportRegisterImpl implements ReportRegister {

  public BeanGetter<AuthDao> authDao;
  public BeanGetter<ClientListReportDao> clientListReportDao;
  public BeanGetter<JdbcSandbox> jdbc;

  private static final int REPORT_ID_LENGTH = 16;

  @Override
  public String saveClientListReportInstance(ClientListReportInstance reportInstance) throws Exception {
    String reportInstanceId = Util.generateRandomString(REPORT_ID_LENGTH);

    clientListReportDao.get().insert(reportInstanceId, reportInstance);

    return reportInstanceId;
  }

  @Override
  public ClientListReportInstance getClientListReportInstance(String reportInstanceId) {
    ClientListReportInstance reportInstance = clientListReportDao.get().selectInstance(reportInstanceId);
    clientListReportDao.get().delete(reportInstanceId);

    return reportInstance;
  }

  @Override
  public void generateClientListReport(ClientListReportInstance reportInstance, ClientListReportView view) {
    UserInfo userInfo = authDao.get().getUserInfo(reportInstance.personId);

    jdbc.get().execute(new GetClientListReport(reportInstance.request,
      Util.getFullname(userInfo.surname, userInfo.name, userInfo.patronymic), view));
  }
}
