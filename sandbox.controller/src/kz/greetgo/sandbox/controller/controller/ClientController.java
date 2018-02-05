package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportViewPdf;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportViewXlsx;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.controller.util.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.*;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ReportRegister> reportRegister;

  @ToJson
  @MethodFilter(GET)
  @Mapping("/count")
  public long getCount(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getCount(request);
  }

  @ToJson
  @MethodFilter(GET)
  @Mapping("/list")
  public List<ClientRecord> getRecordList(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getRecordList(request);
  }

  @MethodFilter(DELETE)
  @Mapping("/remove")
  public void removeRecord(@Par("clientRecordId") long id) {
    clientRegister.get().removeRecord(id);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/details")
  public ClientDetails getDetails(@Par("clientRecordId") Long id) {
    return clientRegister.get().getDetails(id);
  }

  @MethodFilter(POST)
  @Mapping("/save")
  public void saveDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave detailsToSave) {
    clientRegister.get().saveDetails(detailsToSave);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/list/report_instance_id")
  public String getReportInstanceId(@ParSession("personId") String personId,
                                    @Par("clientRecordRequest") @Json ClientRecordRequest request,
                                    @Par("fileContentType") @Json FileContentType fileContentType) throws Exception {
    ClientListReportInstance clientListReportInstance = new ClientListReportInstance();
    clientListReportInstance.personId = personId;
    clientListReportInstance.request = request;
    clientListReportInstance.fileTypeName = fileContentType.name();
    return reportRegister.get().saveClientListReportInstance(clientListReportInstance);
  }

  @NoSecurity
  @MethodFilter(GET)
  @Mapping("/list/report")
  public void streamRecordList(@Par("report_instance_id") @Json String reportInstanceId,
                               RequestTunnel requestTunnel) throws Exception {
    ClientListReportInstance reportInstance =
      reportRegister.get().getClientListReportInstance(reportInstanceId);
    FileContentType fileContentType = FileContentType.valueOf(reportInstance.fileTypeName);

    ClientListReportView reportView;
    String contentType, fileType;
    switch (fileContentType) {
      case PDF:
        contentType = "application/pdf";
        fileType = "pdf";
        reportView = new ClientListReportViewPdf(requestTunnel.getResponseOutputStream());
        break;
      case XLSX:
        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        fileType = "xlsx";
        reportView = new ClientListReportViewXlsx(requestTunnel.getResponseOutputStream());
        break;
      default:
        throw new InvalidParameter();
    }
    requestTunnel.setResponseContentType(contentType);
    requestTunnel.setResponseHeader("Content-Disposition", "attachment; filename=\"" + "client_records" + "_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)) + "." + fileType + "\"");

    reportRegister.get().generateClientListReport(reportInstance, reportView);
    requestTunnel.flushBuffer();
  }
}
