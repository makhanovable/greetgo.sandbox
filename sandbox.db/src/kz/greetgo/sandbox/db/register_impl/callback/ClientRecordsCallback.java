package kz.greetgo.sandbox.db.register_impl.callback;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRequestOptions;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsCallback extends ClientRecordsConnectionCallback<List<ClientRecord>> {

    private ClientRequestOptions options;
    public static BeanGetter<ClientDao> clientDao;
    private static final Logger logger = Logger.getLogger("SERVER");

    public ClientRecordsCallback(ClientRequestOptions options, BeanGetter<ClientDao> clientDao) {
        super(clientDao);
        this.options = options;
        ClientRecordsCallback.clientDao = clientDao;
    }

    @Override
    public List<ClientRecord> doInConnection(Connection connection) throws Exception {
        List<ClientRecord> clientRecords = new ArrayList<>();

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
                while (rs.next())
                    clientRecords.add(extractClientRecord(rs));
            }
        }
        return clientRecords;
    }

}
