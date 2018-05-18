package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;
import kz.greetgo.sandbox.db.migration.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class LoadCiaData {
    private Connection connection;

    public void execute(Connection connection) throws Exception{
        this.connection = connection;

        this.loadData();
    }

    private void loadData() throws Exception{
        ReadXMLFile readXMLFile = new ReadXMLFile();

        List<ClientXMLRecord> clientXMLRecords = readXMLFile.loadData();

        ToXMLParser toXMLParser = new ToXMLParser();

        try (PreparedStatement ps = connection.prepareStatement("insert into transition_client (record_data) values (?)")) {
            for (ClientXMLRecord clientXMLRecord : clientXMLRecords) {
                String recordData = toXMLParser.parseToXML(clientXMLRecord);

                ps.setString(1, recordData);

                ps.execute();

            }
        }
    }

    public static void main (String argv[]) throws Exception {

    }
}
