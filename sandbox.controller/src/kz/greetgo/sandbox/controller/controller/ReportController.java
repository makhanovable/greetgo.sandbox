package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordView;
import kz.greetgo.sandbox.controller.report.ClientRecordViewPDF;
import kz.greetgo.sandbox.controller.report.ClientRecordViewXLSX;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.util.Date;

@Bean
@Mapping("/report")
public class ReportController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @Mapping("/downloadClientReport")
  public void generateReport(@Par("type") String type, @Par("orderBy") String orderBy, @Par("order") int order,
                             @Par("filter") String filter, RequestTunnel requestTunnel) throws Exception {
    if (!("pdf".equals(type) || "xlsx".equals(type))) {
      throw new Exception("Unsupported File Format");
    }

    String filename = "client_report_" + new Date() + "." + type;
    requestTunnel.setResponseHeader("Content-disposition", "attachment; filename=" + filename);
    OutputStream out = requestTunnel.getResponseOutputStream();

    ClientRecordView view;
    switch (type) {
      case "pdf":
        view = new ClientRecordViewPDF(out);
        break;
      case "xlsx":
        view = new ClientRecordViewXLSX(out);
        break;
      default:
        return;
    }

    this.clientRegister.get().generateReport(filter, orderBy, order, view);
    out.flush();
    requestTunnel.flushBuffer();
  }
}
