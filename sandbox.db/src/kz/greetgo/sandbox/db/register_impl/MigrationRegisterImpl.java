package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;

import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.Migration;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationCia;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationFrs;
import kz.greetgo.sandbox.db.register_impl.ssh.SSHConnection;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<JdbcSandbox> jdbcSandbox;


  private boolean isMigrationGoingOn = false;

  final Logger logger = Logger.getLogger(getClass());

  @Override
  public void migrate() throws Exception {
    if (isMigrationGoingOn)
      return;
    isMigrationGoingOn = true;

    Pattern migrationFilePattern = Pattern.compile("(from_cia_(.*).xml.tar.bz2)|(from_frs_(.*).json_row.txt.tar.bz2)");


    List<String> files = getFileNameList(migrationFilePattern);

    while (!files.isEmpty()) {
      String fileName = files.get(0);
      String migrationId = idGenerator.get().newId();
      System.out.println(fileName);

      MigrationConfig config = initMigration(fileName);

      if (config.ready) {
        //
        System.out.println("ready");
        Migration.initMigration(config);
        //
      }

      files = getFileNameList(migrationFilePattern);
    }


    isMigrationGoingOn = false;
  }

  public static void main(String[] args) {
    String tempFileName = Modules.dbDir()+"/build/migration/asdas.txt";
    File file = new File(tempFileName);
    file.getParentFile().mkdirs();
    try (OutputStream out = new FileOutputStream(file)) {
      out.write("asdasdasd".getBytes());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private MigrationConfig initMigration(String fileName) throws Exception {
    MigrationConfig config = new MigrationConfig();

    config.id = idGenerator.get().newId();
    String tempFileName = "build/migration/" + fileName + ".unzipped.migrationId-" + config.id;

    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      if (sshConnection.isFileExist(fileName)) {
        config.afterRenameFileName = fileName + ".migrating" + config.id;
        sshConnection.renameFileName(fileName, config.afterRenameFileName);

        config.toMigrate = new File(tempFileName);

        if (config.toMigrate.getParentFile().mkdirs()) {
          try (OutputStream out = new FileOutputStream(config.toMigrate)) {
            sshConnection.downloadFile(config.afterRenameFileName, out);
          }
        } else {
          return config;
        }
      } else
        return config;
    }
    DbConfig dbConfig = allConfigFactory.get().createPostgresDbConfig();
    config.dbConfig = dbConfig;
    config.ready = true;
    return config;
  }

  private List<String> getFileNameList(Pattern pattern) throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      files = sshConnection.getFileNameList(".");
    }
    return files.stream().filter(o -> pattern.matcher(o).matches()).collect(Collectors.toList());
  }


}
