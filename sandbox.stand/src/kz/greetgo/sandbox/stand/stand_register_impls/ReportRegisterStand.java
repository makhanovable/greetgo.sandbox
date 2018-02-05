package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.stand.model.ReportInstanceDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ReportRegisterStand implements ReportRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<ClientRegister> clientRegister;

  @Override
  public String saveClientListReportInstance(ClientListReportInstance reportInstance) throws Exception {
    String reportIdInstance = Util.generateRandomString(16);

    ReportInstanceDot reportInstanceDot = new ReportInstanceDot();
    reportInstanceDot.reportIdInstance = reportIdInstance;
    reportInstanceDot.personId = reportInstance.personId;
    reportInstanceDot.request = ClientRecordRequest.serialize(reportInstance.request);
    reportInstanceDot.fileTypeName = reportInstance.fileTypeName;

    db.get().clientListReportStorage.put(reportIdInstance, reportInstanceDot);

    return reportIdInstance;
  }

  @Override
  public ClientListReportInstance getClientListReportInstance(String reportInstanceId) throws Exception {
    ReportInstanceDot reportInstanceDot = db.get().clientListReportStorage.get(reportInstanceId);
    if (reportInstanceDot == null) throw new AuthError("Invalid report id");

    ClientListReportInstance reportInstance = new ClientListReportInstance();
    reportInstance.personId = reportInstanceDot.personId;
    reportInstance.request = ClientRecordRequest.deserialize(reportInstanceDot.request);
    reportInstance.fileTypeName = reportInstanceDot.fileTypeName;

    db.get().clientListReportStorage.remove(reportInstanceId);

    return reportInstance;
  }

  @Override
  public void generateClientListReport(ClientListReportInstance reportInstance, ClientListReportView view) throws Exception {
    PersonDot personDot = db.get().personStorage.get(reportInstance.personId);

    generateReportView(view, reportInstance.request,
      Util.getFullname(personDot.surname, personDot.name, personDot.patronymic));
  }

  private void generateReportView(ClientListReportView reportView, ClientRecordRequest request, String authorName) throws Exception {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = PageUtils.getFilteredList(clientDots, request.nameFilter);
    clientDots = PageUtils.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    ReportHeaderData headerData = new ReportHeaderData();
    headerData.columnSortType = request.columnSortType;
    reportView.start(headerData);

    for (ClientDot clientDot : clientDots)
      reportView.append(clientDotToReportItemData(clientDot));

    ReportFooterData reportFooterData = new ReportFooterData();
    reportFooterData.createdAt = Date.from(Instant.now());
    reportFooterData.createdBy = authorName;
    reportView.finish(reportFooterData);
  }

  private ReportItemData clientDotToReportItemData(ClientDot clientDot) throws Exception {
    ReportItemData ret = new ReportItemData();

    ret.fullname = Util.getFullname(clientDot.surname, clientDot.name, clientDot.patronymic);
    ret.charmName = clientDot.charm.name;
    ret.age = clientDot.age;
    ret.totalAccountBalance = Util.stringToFloat(clientDot.totalAccountBalance);
    ret.maxAccountBalance = Util.stringToFloat(clientDot.maxAccountBalance);
    ret.minAccountBalance = Util.stringToFloat(clientDot.minAccountBalance);

    return ret;
  }
}
