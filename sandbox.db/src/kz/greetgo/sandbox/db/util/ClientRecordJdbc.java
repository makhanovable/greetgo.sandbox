package kz.greetgo.sandbox.db.util;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientRecordView;
import kz.greetgo.sandbox.db.SqlBuilder.ClientRecordQueryBuilder;
import kz.greetgo.sandbox.db.SqlBuilder.ClientRecordReportViewQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;

public class ClientRecordJdbc implements ConnectionCallback<Void> {

  private final ClientRecordView view;
  public Map<String, String> charms;

  private int limit = -1;
  private int offset = -1;
  private ClientRecordQueryBuilder query;
  private String filter;

  public ClientRecordJdbc(ClientRecordQueryBuilder query, String filter, String orderBy, int order, ClientRecordView view) {

    this.query = query;
    this.filter = filter;

    this.view = view;

    this.filter = ClientUtils.getFormattedFilter(filter);
    String[] orders = ClientUtils.sortableColumns;
    boolean orderByIsMatch = orderBy != null && Arrays.stream(orders).anyMatch(o -> o.equals(orderBy));

    query.withFilter = filter != null;
    if (orderByIsMatch)
      query.orderBy = orderBy;

    query.desc = order == 1;
  }

  public ClientRecordJdbc(ClientRecordQueryBuilder query, String filter, String orderBy, int order, int limit, int offset, ClientRecordView view) {
    this(query, filter, orderBy, order, view);
    this.limit = limit;
    this.offset = offset;
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    StringBuilder sb = new StringBuilder();
    query.generateSql(sb);
    try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {

      int argIndex = 1;
      if (filter != null)
        ps.setString(argIndex++, filter);
      if (limit != -1) {
        ps.setInt(argIndex++, limit);
        ps.setInt(argIndex, offset);
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {

          ClientRecord clientRecord = ClientUtils.rsToClientRecord(rs);
          if (charms != null)
            clientRecord.charm = this.charms.get(clientRecord.charm);
          view.appendRow(clientRecord);
        }
        return null;
      }
    }
  }


}
