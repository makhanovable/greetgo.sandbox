package kz.greetgo.sandbox.db.register_impl.callback;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.RequestOptions;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientRecordsReportCallback extends ClientRecordsConnectionCallback<Void> {

    private RequestOptions options;
    private ClientRecordsReportView view;
    public BeanGetter<ClientDao> clientDao;
    private static final Logger logger = Logger.getLogger(ClientRecordsReportCallback.class);

    public ClientRecordsReportCallback(RequestOptions options, BeanGetter<ClientDao> clientDao,
                                       ClientRecordsReportView view) {
        super(clientDao);
        this.options = options;
        this.clientDao = clientDao;
        this.view = view;
    }

    @Override
    public Void doInConnection(Connection connection) throws Exception {
        String sql = createSqlForGetClientRecords(options);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // START set params to PreparedStatement
            ps.setString(1, "%" + options.filter + "%");
            ps.setString(2, "%" + options.filter + "%");
            ps.setString(3, "%" + options.filter + "%");

            if (options.page != null && options.size != null) {
                ps.setInt(4, Integer.parseInt(options.size));
                ps.setInt(5, Integer.parseInt(options.page) * Integer.parseInt(options.size));
            }
            // END set params to PreparedStatement

            logger.info("Executed SQL : " + ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    view.append(extractClientRecordReport(rs));
                }
            }
        }
        return null;
    }

}
