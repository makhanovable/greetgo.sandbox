package kz.greetgo.sandbox.db.register_impl.callback;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordInfo;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;

public class ClientRecordsCallback implements ConnectionCallback<ClientRecordInfo> {

    public BeanGetter<ClientDao> clientDao;
    private Options options;

    public ClientRecordsCallback(Options options, BeanGetter<ClientDao> clientDao) {
        this.options = options;
        this.clientDao = clientDao;
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
                while (rs.next()) {
                    ClientRecord clientRecord = new ClientRecord();
                    clientRecord.id = rs.getInt("id");
                    clientRecord.name = rs.getString("name");
                    clientRecord.age = calculateAge(rs.getString("age"));
                    clientRecord.charm = clientDao.get().getCharmById(rs.getInt("charm"));
                    clientRecord.total = rs.getFloat("total");
                    clientRecord.min = rs.getFloat("min");
                    clientRecord.max = rs.getFloat("max");
                    clientRecordInfo.total_count = rs.getInt("count");
                    clientRecords.add(clientRecord);
                }
                clientRecordInfo.items = clientRecords;
            }
        }
        return clientRecordInfo;
    }

    public static String createSqlForGetClientRecords(Options options) {
        String sql = "WITH info (id, iname, surname, patronymic, gender, charm, birth_date) AS (" +
                " SELECT id, name as iname, surname, patronymic, gender, charm, birth_date" +
                " FROM client WHERE actual = TRUE AND (name LIKE ? " +
                " OR surname LIKE ? OR patronymic LIKE ?))" +
                " SELECT info.id, concat_ws(' ', info.iname, info.surname, info.patronymic) AS name," +
                " info.gender, info.charm," +
                " info.birth_date AS age, CASE WHEN (SELECT min(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) ISNULL THEN 0 ELSE " +
                " (SELECT min(client_account.money) FROM client_account WHERE client_account.client = info.id)" +
                " END AS min, CASE WHEN (SELECT max(client_account.money)" +
                " FROM client_account WHERE client_account.client = info.id) ISNULL THEN 0 ELSE " +
                " (SELECT max(client_account.money) FROM client_account WHERE client_account.client = info.id)" +
                " END AS max, CASE WHEN (SELECT sum(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) ISNULL THEN 0 ELSE (SELECT sum(client_account.money)" +
                " FROM client_account WHERE client_account.client = info.id) END AS total," +
                " (SELECT count(info) FROM info) AS count FROM info";

        if (isValidSortOptions(options.sort, options.order)) {
            sql += " ORDER BY " + options.sort;
            if (options.sort.equalsIgnoreCase("age")) {
                if (options.order.equalsIgnoreCase("asc"))
                    sql+= " DESC";
            } else if (options.order.equalsIgnoreCase("asc"))
                sql+= " NULLS FIRST";
            else
                sql+= " DESC NULLS LAST";
        }

        if (options.page != null && options.size != null)
            sql += " LIMIT ? OFFSET ?";
        return sql;
    }

    public static boolean isValidSortOptions(String sort, String order) {
        return sort != null
                && order != null
                && (sort.equalsIgnoreCase("name") ||
                sort.equalsIgnoreCase("age") ||
                sort.equalsIgnoreCase("total") ||
                sort.equalsIgnoreCase("max") ||
                sort.equalsIgnoreCase("min"))
                && (order.equalsIgnoreCase("desc") ||
                order.equalsIgnoreCase("asc"));
    }

}
