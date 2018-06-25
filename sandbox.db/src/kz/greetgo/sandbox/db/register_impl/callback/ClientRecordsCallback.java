package kz.greetgo.sandbox.db.register_impl.callback;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordInfo;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.model.SortBy;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;

public class ClientRecordsCallback implements ConnectionCallback<ClientRecordInfo> {

    public static BeanGetter<ClientDao> clientDao;
    private Options options;

    public ClientRecordsCallback(Options options, BeanGetter<ClientDao> clientDao) {
        this.options = options;
        ClientRecordsCallback.clientDao = clientDao;
    }

    @Override
    public ClientRecordInfo doInConnection(Connection connection) throws Exception {
        ClientRecordInfo clientRecordInfo = new ClientRecordInfo();
        List<ClientRecord> clientRecords = new ArrayList<>();
        clientRecordInfo.items = clientRecords;

        options.filter = options.filter != null ? options.filter : "";
        String sql = createSqlForGetClientRecords(options);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // START set params to PreparedStatement
            ps.setString(1, "%" + options.filter + "%");
            ps.setString(2, "%" + options.filter + "%");
            ps.setString(3, "%" + options.filter + "%");

            if (options.page != null && options.size != null) {
                ps.setBigDecimal(4, new BigDecimal(options.size));
                ps.setBigDecimal(5, new BigDecimal(options.page)
                        .multiply(new BigDecimal(options.size)));
            }
            // END set params to PreparedStatement

            System.out.println(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    clientRecords.add(extractClientRecord(rs));
                clientRecordInfo.items = clientRecords;
                clientRecordInfo.total_count = clientDao.get().getClientRecordsCount(options.filter);
            }
        }
        return clientRecordInfo;
    }

    static ClientRecord extractClientRecord(ResultSet rs) throws SQLException {
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id = rs.getInt("id");
        clientRecord.name = rs.getString("name");
        clientRecord.age = calculateAge(rs.getString("age"));
        clientRecord.charm = clientDao.get().getCharmById(rs.getInt("charm"));
        clientRecord.total = rs.getFloat("total");
        clientRecord.min = rs.getFloat("min");
        clientRecord.max = rs.getFloat("max");
        return clientRecord;
    }

    static String createSqlForGetClientRecords(Options options) {
        StringBuilder sb = new StringBuilder();

        sb.append("WITH info (id, iname, surname, patronymic, gender, charm, birth_date) ");
        sb.append("AS (SELECT id, name as iname, surname, patronymic, gender, charm, birth_date ");
        sb.append("FROM client WHERE actual = TRUE AND (name LIKE ? OR surname LIKE ? OR patronymic LIKE ?)) ");

        sb.append("SELECT info.id, concat_ws(' ', info.iname, info.surname, info.patronymic) AS name, ");
        sb.append("info.gender, info.charm, info.birth_date AS age, ");

        sb.append("CASE WHEN (SELECT min(client_account.money) FROM client_account ");
        sb.append("WHERE client_account.client = info.id) ISNULL THEN 0 ELSE ");
        sb.append("(SELECT min(client_account.money) FROM client_account WHERE client_account.client = info.id) END AS min, ");

        sb.append("CASE WHEN (SELECT max(client_account.money) FROM client_account ");
        sb.append("WHERE client_account.client = info.id) ISNULL THEN 0 ELSE ");
        sb.append("(SELECT max(client_account.money) FROM client_account WHERE client_account.client = info.id) END AS max, ");

        sb.append("CASE WHEN (SELECT sum(client_account.money) FROM client_account ");
        sb.append("WHERE client_account.client = info.id) ISNULL THEN 0 ELSE ");
        sb.append("(SELECT sum(client_account.money) FROM client_account WHERE client_account.client = info.id) END AS total ");

        sb.append("FROM info");


        if (options.sort != null && options.order != null) {
            sb.append(" ORDER BY ").append(options.sort);
            if (options.sort == SortBy.age) {
                if (options.order.equalsIgnoreCase("asc"))
                    sb.append(" DESC");
            } else if (options.order.equalsIgnoreCase("desc"))
                sb.append(" DESC");
        }

        if (options.page != null && options.size != null)
            sb.append(" LIMIT ? OFFSET ?");
        return sb.toString();
    }

}
