package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.conf.SysParams;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.util.App;
import kz.greetgo.sandbox.db.util.LiquibaseManager;
import kz.greetgo.util.ServerUtil;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import static kz.greetgo.sandbox.db.test.util.DbUrlUtils.changeUrlDbName;
import static kz.greetgo.sandbox.db.test.util.DbUrlUtils.extractDbName;

@Bean
public class DbWorker {
    final Logger logger = Logger.getLogger(getClass());

    public BeanGetter<DbConfig> postgresDbConfig;
    public BeanGetter<AllConfigFactory> allPostgresConfigFactory;
    public BeanGetter<LiquibaseManager> liquibaseManager;

    public void recreateAll() throws Exception {
        prepareDbConfig();
        recreateDb();

        liquibaseManager.get().apply();
        App.do_not_run_liquibase_on_deploy_war().createNewFile();
    }

    private final java.util.Set<String> alreadyRecreatedUsers = new HashSet<>();

    private void recreateDb() throws Exception {

        final String dbName = extractDbName(postgresDbConfig.get().url());
        final String username = postgresDbConfig.get().username();
        final String password = postgresDbConfig.get().password();

        try (Connection con = getPostgresAdminConnection()) {

            try (Statement stt = con.createStatement()) {
                logger.info("drop database " + dbName);
                stt.execute("drop database " + dbName);
            } catch (PSQLException e) {
                System.err.println(e.getServerErrorMessage());
            }

            if (!alreadyRecreatedUsers.contains(username)) {
                alreadyRecreatedUsers.add(username);

                try (Statement stt = con.createStatement()) {
                    logger.info("drop user " + username);
                    stt.execute("drop user " + username);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    //ignore
                }

                try (Statement stt = con.createStatement()) {
                    logger.info("create user " + username + " encrypted password '" + password + "'");
                    stt.execute("create user " + username + " encrypted password '" + password + "'");
                } catch (PSQLException e) {
                    ServerErrorMessage sem = e.getServerErrorMessage();
                    if ("CreateRole".equals(sem.getRoutine())) {
                        throw new RuntimeException("Невозможно создать пользователя " + username + ". Возможно кто-то" +
                                " приконектился к базе данных под этим пользователем и поэтому он не удаляется." +
                                " Попробуйте разорвать коннект с БД или перезапустить БД. После повторите операцию снова", e);
                    }

                    throw e;
                }
            }

            try (Statement stt = con.createStatement()) {
                logger.info("create database " + dbName);
                stt.execute("create database " + dbName);
            }
            try (Statement stt = con.createStatement()) {
                logger.info("grant all on database " + dbName + " to " + username);
                stt.execute("grant all on database " + dbName + " to " + username);
            }
        }
    }


    public void cleanConfigsForTeamcity() {
        if (System.getProperty("user.name").startsWith("teamcity")) {
            ServerUtil.deleteRecursively(App.appDir());
        }
    }

    private void prepareDbConfig() throws Exception {
        File file = allPostgresConfigFactory.get().storageFileFor(DbConfig.class);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            writeDbConfigFile();
        } else if ("null".equals(postgresDbConfig.get().url())) {
            writeDbConfigFile();
            allPostgresConfigFactory.get().reset();
        }
    }

    private void writeDbConfigFile() throws Exception {
        File file = allPostgresConfigFactory.get().storageFileFor(DbConfig.class);
        try (PrintStream out = new PrintStream(file, "UTF-8")) {

            out.println("url=" + changeUrlDbName(SysParams.pgAdminUrl(), System.getProperty("user.name") + "_sandbox"));
            out.println("username=" + System.getProperty("user.name") + "_sandbox");
            out.println("password=111");

//      out.println("url=jdbc:postgresql://localhost:5432/sandbox");// + changeUrlDbName(SysParams.pgAdminUrl(), System.getProperty("user.name") + "_sandbox"));
//      out.println("username=sandbox_user");// + System.getProperty("user.name") + "_sandbox");
//      out.println("password=sandbox");

        }
    }

    public static Connection getPostgresAdminConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
//    return DriverManager.getConnection(
//            SysParams.pgAdminUrl(),
//            SysParams.pgAdminUserid(),
//            SysParams.pgAdminPassword()
//    );
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/makhan_sandbox",
                "makhan_sandbox",
                "111"
        );
    }
}
