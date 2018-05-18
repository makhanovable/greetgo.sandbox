package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.db.migration.core.LoadCiaData;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.*;

public class MigrationTest extends ParentTestNg{
    Connection connection;

    @BeforeTest
    public void beforeTest() throws Exception {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/s_sandbox",
                "s_sandbox",
                "password"
        );
    }

    @Test
    public void testParser() throws Exception{
        LoadCiaData loadCiaData = new LoadCiaData();
        loadCiaData.execute(connection);
    }

    @AfterTest
    public void afterTest() throws Exception {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }
}
