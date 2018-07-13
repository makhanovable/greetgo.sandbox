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
    private static final Logger logger = Logger.getLogger("SQL_queries");

    MigrationAbstract(Connection connection) {
        this.connection = connection;
    }

    public abstract void migrate() throws Exception;

    void exec(String sql) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("NOW EXECUTING " + sql);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }

        long end = System.currentTimeMillis();
        System.out.println("Time to execute SQL ABOVE " + (end - start) + " ms");
        topSqlQueries.put(sql, end - start);
    }

    void loadTopSqlQueriesList() {
        logger.info("Start Session");
        Map<String, Long> sorted = ClientHelperUtil.sortMapByValues(topSqlQueries);
        for (String key : sorted.keySet()) {
            logger.info(sorted.get(key) + "ms - " + key);
        }

        logger.info("End Session");
        logger.info("");
    }

}
