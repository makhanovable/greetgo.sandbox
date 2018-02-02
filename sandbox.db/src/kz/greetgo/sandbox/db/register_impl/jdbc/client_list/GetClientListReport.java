package kz.greetgo.sandbox.db.register_impl.jdbc.client_list;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;

public class GetClientListReport extends GetClientList {

  private final OutputStream outputStream;
  private final ClientRecordRequest request;
  private final ClientListReportView reportView;
  private final String authorName;

  public GetClientListReport(OutputStream outputStream, ClientRecordRequest request, String authorName,
                             ClientListReportView reportView) {
    super(request);

    this.outputStream = outputStream;
    this.request = request;
    this.authorName = authorName;
    this.reportView = reportView;
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();

    ReportHeaderData headerData = new ReportHeaderData();
    headerData.columnSortType = request.columnSortType;
    reportView.start(outputStream, headerData);

    try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
      int index = 1;

      for (Object sqlParam : sqlParamList)
        ps.setObject(index++, sqlParam);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next())
          reportView.append(rsToReportItemData(rs));
      }
    }

    ReportFooterData reportFooterData = new ReportFooterData();
    reportFooterData.createdAt = Date.from(Instant.now());
    reportFooterData.createdBy = this.authorName;
    reportView.finish(reportFooterData);

    return null;
  }

  @Override
  protected void limit() {
  }

  protected ReportItemData rsToReportItemData(ResultSet rs) throws Exception {
    ReportItemData ret = new ReportItemData();

    ret.fullname = Util.getFullname(rs.getString("surname"), rs.getString("name"), rs.getString("patronymic"));
    ret.charmName = rs.getString("charmName");
    ret.age = rs.getInt("age");
    ret.totalAccountBalance = rs.getFloat("totalAccountBalance");
    ret.maxAccountBalance = rs.getFloat("maxAccountBalance");
    ret.minAccountBalance = rs.getFloat("minAccountBalance");

    return ret;
  }
}
