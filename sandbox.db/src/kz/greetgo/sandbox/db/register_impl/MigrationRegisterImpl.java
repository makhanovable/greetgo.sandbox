package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;

import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.register_impl.migration.Migration;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.ssh.SSHConnection;
import kz.greetgo.sandbox.db.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static kz.greetgo.sandbox.db.register_impl.migration.Migration.getCiaFileNamePattern;
import static kz.greetgo.sandbox.db.register_impl.migration.Migration.getFrsFileNamePattern;

@SuppressWarnings("unused")
@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<AllConfigFactory> allConfigFactory;
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;

  private AtomicBoolean isMigrationGoingOn = new AtomicBoolean(false);
  private final Logger logger = Logger.getLogger(getClass());

  @Override
  public void migrate() throws Exception {

    if (isMigrationGoingOn.get())
      return;
    isMigrationGoingOn.set(true);

    @SuppressWarnings("DuplicateAlternationBranch")
    Pattern migrationFilePattern = Pattern.compile("(" + getCiaFileNamePattern() + ")|(" + getFrsFileNamePattern() + ")");
    List<String> files = getFileNameList(migrationFilePattern);

    while (!files.isEmpty()) {
      String fileName = files.get(0);

      MigrationConfig config = initConfig(fileName);
      DbConfig dbConfig = allConfigFactory.get().createPostgresDbConfig();

      if (config != null) {
        config.idGenerator = idGenerator.get();

//        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(
          dbConfig.url(),
          dbConfig.username(),
          dbConfig.password())) {

          Migration.getMigrationInstance(config).migrate(connection);
        }

        try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
          String finishedMigrationFileName = config.afterRenameFileName.replace("migrating", "migrated-");
          sshConnection.renameFileName(config.afterRenameFileName, finishedMigrationFileName);
          if (config.error.exists() && config.error.length() != 0)
            sshConnection.uploadFile(config.error);
        }

      }

      files = getFileNameList(migrationFilePattern);
    }


    isMigrationGoingOn.set(false);
  }


  private MigrationConfig initConfig(String fileName) throws Exception {

    MigrationConfig config = new MigrationConfig();

    config.id = idGenerator.get().newId();
    config.originalFileName = fileName;
    String tempFileName = Modules.dbDir() + "/build/migration/" + fileName;

    File copiedFile = new File(tempFileName);
    //noinspection ResultOfMethodCallIgnored
    copiedFile.getParentFile().mkdirs();

    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {

      if (sshConnection.isFileExist(fileName)) {

        config.afterRenameFileName = fileName + ".migrating" + config.id;
        sshConnection.renameFileName(fileName, config.afterRenameFileName);

        if (sshConnection.isFileExist(config.afterRenameFileName)) {
          try (OutputStream out = new FileOutputStream(copiedFile)) {
            sshConnection.downloadFile(config.afterRenameFileName, out);
          }
        } else {
          logger.trace("file " + config.afterRenameFileName + " no longer exist after renamed");
          return null;
        }
      } else {
        logger.trace("file " + fileName + " not found in ssh directory");
        return null;
      }
    }

    String decompressed = copiedFile.getPath().replaceAll(".bz2", "");
    File decompressedFile = new File(decompressed);
    FileUtils.decompressFile(copiedFile, decompressedFile);
    if (!decompressedFile.exists()) {
      logger.trace("cant decompress file");
      return null;
    }

    if (!copiedFile.delete()) {
      logger.trace("could not delete file " + copiedFile.getPath());
    }


    String untarred = decompressed.replaceAll(".tar", "");
    File untareedFile = new File(untarred);
    FileUtils.untarFile(decompressedFile, untareedFile);
    if (!untareedFile.exists()) {
      logger.trace("cant untar file");
      return null;
    }

    if (!decompressedFile.delete()) {
      logger.trace("could not delete file " + decompressedFile.getPath());
    }

    config.toMigrate = untareedFile;
    config.error = new File(Modules.dbDir() + "/build/migration/" + fileName + ".error");

    return config;
  }


  private List<String> getFileNameList(Pattern pattern) throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      files = sshConnection.getFileNameList(".");
    }
    if (files == null)
      return new ArrayList<>();

    return files.stream().filter(o -> pattern.matcher(o).matches()).collect(Collectors.toList());
  }


}
