package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ReportInstanceDot;

import java.io.OutputStream;

@Bean
public class ReportRegisterStand implements ReportRegister {

  public BeanGetter<StandDb> db;

  @Override
  public String save(String personId, ClientRecordRequest request, FileContentType fileContentType) throws Exception {
    String reportIdInstance = Util.generateRandomString(16);

    ReportInstanceDot reportInstanceDot = new ReportInstanceDot();
    reportInstanceDot.reportIdInstance = reportIdInstance;
    reportInstanceDot.personId = personId;
    reportInstanceDot.request = ClientRecordRequest.serialize(request);
    reportInstanceDot.fileTypeName = fileContentType.name();

    db.get().clientListReportStorage.put(reportIdInstance, reportInstanceDot);

    return reportIdInstance;
  }

  @Override
  public ClientListReportInstance checkForValidity(String reportIdInstance) throws Exception {
    ReportInstanceDot reportInstanceDot = db.get().clientListReportStorage.get(reportIdInstance);
    if (reportInstanceDot == null) throw new AuthError("Invalid report id");

    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.reportIdInstance = reportInstanceDot.reportIdInstance;
    reportInstance.personId = reportInstanceDot.personId;
    reportInstance.request = reportInstanceDot.request.clone();
    reportInstance.fileTypeName = reportInstanceDot.fileTypeName;

    db.get().clientListReportStorage.remove(reportIdInstance);

    return reportInstance;
  }

  @Override
  public void prepareForGeneration(RequestTunnel requestTunnel, String fileName, FileContentType fileContentType) {

  }

  @Override
  public void generate(ClientListReportView reportView, String personId, ClientRecordRequest request) {

  }

}
