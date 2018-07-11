package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.util.ClientHelperUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public abstract class MigrationAbstract {

    private Connection connection;
    private Map<String, Long> topSqlQueries = new HashMap<>();
    private static final Logger logger = Logger.getLogger(MigrationAbstract.class);

    public MigrationAbstract(Connection connection) {
        this.connection = connection;
    }

    public abstract void migrate() throws Exception;

    public void exec(String sql) {
        long start = System.currentTimeMillis();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        topSqlQueries.put(sql, end - start);
    }

    public void loadTopSqlQueriesList() {
        Map<String, Long> sorted = ClientHelperUtil.sortMapByValues(topSqlQueries);
        for (String key : sorted.keySet()) {
            logger.info(sorted.get(key) + " - " + key + "\n");
        }
    }

}
