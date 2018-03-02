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

import java.util.List;
import java.util.regex.Pattern;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;
  private boolean isMigrationGoingOn = false;

  final Logger logger = Logger.getLogger(getClass());

  @Override
  public void migrate() throws Exception {
    if (isMigrationGoingOn)
      return;
    isMigrationGoingOn = true;

    SshConfig sshConfig = allConfigFactory.get().createSshConfig();
    DbConfig dbConfig = allConfigFactory.get().createPostgresDbConfig();


    Pattern cia = Pattern.compile("from_cia_(.*).xml");
    Pattern frs = Pattern.compile("from_frs_(.*).json_row");
    List<String> files = getFileNameList();

    for (String filename : files) {
      Migration migration = null;
      if (cia.matcher(filename).matches()) {
        migration = new MigrationCia(dbConfig);
      } else if (frs.matcher(filename).matches()) {
        migration = new MigrationFrs(dbConfig);
      }
      if (migration != null) {
        migration.migrate();
      }

    }

    isMigrationGoingOn = false;
  }

  private List<String> getFileNameList() throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      files = sshConnection.getFileNameList(".");
    }
    return files;
  }


  public static void main(String[] args) throws Exception {
    SshConfig sshConfig = new SshConfig() {
      @Override
      public String host() {
        return "127.0.0.1";
      }

      @Override
      public int port() {
        return 22;
      }

      @Override
      public String username() {
        return "damze";
      }

      @Override
      public String password() {
        return "111";
      }

      @Override
      public String migrationDir() {
        return "var/metodology";
      }
    };

    try (SSHConnection sshConnection = new SSHConnection(sshConfig)) {
      List<String> files = sshConnection.getFileNameList(".");
      for (String filaname : files) {
        System.out.println(filaname);
      }


    }


  }

}
