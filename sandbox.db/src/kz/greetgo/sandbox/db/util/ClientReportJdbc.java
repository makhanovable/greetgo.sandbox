package kz.greetgo.sandbox.db.util;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.report.ClientReportView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;

public class ClientReportJdbc implements ConnectionCallback<Void> {

  // FIXME: 2/23/18 Код для web-view, количества клиентов и отчета должен быть один
  // FIXME: 2/23/18 На отчеты теста не нашел
  private final ClientReportView view;
  private final String filter;
  private final String orderBy;
  private final int order;
  private final Map<String, String> charms;


  public ClientReportJdbc(String filter, String orderBy, int order, ClientReportView view, Map<String, String> charms) {
    this.view = view;
    this.filter = filter;
    this.orderBy = orderBy;
    this.order = order;
    this.charms = charms;
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {

    try (PreparedStatement ps = connection.prepareStatement(generateSql().toString())) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {

          ClientRecord clientRecord = ClientUtils.rsToClientRecord(rs);
          clientRecord.charm = this.charms.get(clientRecord.charm);
          view.appendRow(clientRecord);
        }
        return null;
      }
    }
  }

  private StringBuilder generateSql() {
    String queryFilter = ClientUtils.getFormattedFilter(filter);

    Boolean match = orderBy != null && Arrays.stream(ClientUtils.sortableColumns).anyMatch(o -> o.equals(orderBy));

    StringBuilder query = new StringBuilder();

    query.append("select c.id, c.name, c.surname, c.patronymic, date_part('year',age(c.birthDate)) as age, c.charm, ca.totalAccountBalance, ca.maximumBalance, ca.minimumBalance");
    query.append(" from (select * from Client where actual=true");
    if (queryFilter != null)
      query.append(" and lower(concat(name, surname, patronymic)) SIMILAR TO ?");
    query.append(") c");
    query.append(" left join (select client, max(money) maximumBalance, min(money) minimumBalance, sum(money) totalAccountBalance from ClientAccount group by client) ca on ca.client=c.id");
    if (match)
      query.append(" order by ").append(orderBy);
    else
      query.append(" order by concat(name, surname, patronymic)");
    if (order == 1)
      query.append(" desc");

    return query;
  }


}
