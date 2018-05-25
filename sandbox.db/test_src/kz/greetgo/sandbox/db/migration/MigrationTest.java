package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationTest extends ParentTestNg{
    Connection connection;

    @BeforeTest
    private void createConnection() throws Exception{
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/s_sandbox",
                "s_sandbox",
                "password"
        );
    }

    @Test
    public void TestTableCreation() {

    }


    private void clearTables() throws Exception{
        String sql = "delete ";

        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    @AfterTest
    private void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.connection = null;
        }
    }
}
