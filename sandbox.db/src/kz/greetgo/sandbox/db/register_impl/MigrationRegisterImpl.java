package kz.greetgo.sandbox.db.register_impl;

import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;

import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.register_impl.migration.Migration;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationCia;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationFrs;
import kz.greetgo.sandbox.db.register_impl.ssh.SSHConnection;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;
  public BeanGetter<IdGenerator> idGenerator;

  private boolean isMigrationGoingOn = false;

  final Logger logger = Logger.getLogger(getClass());

  @Override
  public void migrate() throws Exception {
    if (isMigrationGoingOn)
      return;
    isMigrationGoingOn = true;

    SshConfig sshConfig = allConfigFactory.get().createSshConfig();
    DbConfig dbConfig = allConfigFactory.get().createPostgresDbConfig();


    List<String> files = getFileNameList();

    Pattern cia = Pattern.compile("from_cia_(.*).xml");
    Pattern frs = Pattern.compile("from_frs_(.*).json_row");
    for (String filename : files) {
      Migration migration = null;
      if (cia.matcher(filename).matches()) {
        migration = new MigrationCia(dbConfig);
      } else if (frs.matcher(filename).matches()) {
        migration = new MigrationFrs(dbConfig);
      }

      if (migration != null) {

        this.doMigration(filename, migration);
      }

    }

    isMigrationGoingOn = false;
  }


  private void doMigration(String fileName, Migration migration) throws Exception {
    String tempFileName = "random//TODO";

    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      if (sshConnection.isFileExist(fileName)) {
        String fileNameToRename = fileName + ".migrating" + idGenerator.get().newId();
        sshConnection.renameFileName(fileName, fileNameToRename);

        File localFileToMigrate = new File(tempFileName);
        if (localFileToMigrate.mkdirs()) {

          try (OutputStream out = new FileOutputStream(localFileToMigrate)) {
            sshConnection.downloadFile(fileNameToRename, out);
          }

          migration.migrate();
        }

      } else
        return;
    }


  }


  private List<String> getFileNameList() throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      files = sshConnection.getFileNameList(".");
    }
    return files;
  }


}
