package kz.greetgo.sandbox.db.register_impl.callback;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static kz.greetgo.sandbox.db.register_impl.callback.ClientRecordsCallback.createSqlForGetClientRecords;
import static kz.greetgo.sandbox.db.register_impl.callback.ClientRecordsCallback.extractClientRecord;

public class ClientRecordsReportCallback implements ConnectionCallback<Void> {

    public BeanGetter<ClientDao> clientDao;
    private Options options;
    private ClientRecordsReportView view;

    public ClientRecordsReportCallback(Options options, BeanGetter<ClientDao> clientDao,
                                       ClientRecordsReportView view) {
        this.options = options;
        this.clientDao = clientDao;
        this.view = view;
    }

    @Override
    public Void doInConnection(Connection connection) throws Exception {
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
                    view.append(extractClientRecord(rs));
            }
        }
        return null;
    }

}
