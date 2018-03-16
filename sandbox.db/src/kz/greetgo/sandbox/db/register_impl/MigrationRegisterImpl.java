package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.register_impl.migration.Migration;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.migration.exception.UnsupportedFileExtension;
import kz.greetgo.sandbox.db.register_impl.ssh.SSHConnection;
import kz.greetgo.sandbox.db.util.DateUtils;
import kz.greetgo.sandbox.db.util.DbUtils;
import kz.greetgo.sandbox.db.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static kz.greetgo.sandbox.db.register_impl.migration.Migration.getCiaFileNamePattern;
import static kz.greetgo.sandbox.db.register_impl.migration.Migration.getFrsFileNamePattern;
import static kz.greetgo.sandbox.db.register_impl.ssh.SshUtil.getFileNameList;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<IdGenerator> idGenerator;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<DbConfig> dbConfigBeanGetter;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<SshConfig> sshConfigBeanGetter;

  private AtomicBoolean isMigrationGoingOn = new AtomicBoolean(false);
  private final Logger logger = Logger.getLogger("MIGRATION");

  // TODO: 3/16/18 Миграция должна запускаться по ссылке. Нужен контролер MigrationController

  @Override
  public void migrate() throws Exception {

    if (isMigrationGoingOn.get())
      return;
    isMigrationGoingOn.set(true);

    Date started = new Date();
    String date = DateUtils.getDateWithTimeString(started);

    logger.info("##########################################################");
    logger.info("MIGRATION STARTED ");
    logger.info("STARTED DATE,TIME: " + date);
    logger.info("##########################################################");

    @SuppressWarnings("DuplicateAlternationBranch")
    Pattern migrationFilePattern = Pattern.compile("(" + getCiaFileNamePattern() + ")|(" + getFrsFileNamePattern() + ")");
    List<String> files = getFileNameList(migrationFilePattern, sshConfigBeanGetter.get());

    DbConfig dbConfig = dbConfigBeanGetter.get();

    while (!files.isEmpty()) {

      for (String fileName : files) {

        MigrationConfig config = initMigrationConfig(fileName, idGenerator.get());
        String migratedFilePostfix = "migrated-";

        if (config != null) {

          try (Connection connection = DbUtils.getPostgresConnection(dbConfig.url(), dbConfig.username(), dbConfig.password())) {

            Migration.getMigrationInstance(config, connection).migrate();

          } catch (UnsupportedFileExtension e) {
            logger.fatal("Error with file format", e);
            migratedFilePostfix = "notMigrated-";
          } catch (Exception e) {
            logger.fatal("unexpected exception:", e);
            throw e;
          }

          try (SSHConnection sshConnection = new SSHConnection(sshConfigBeanGetter.get())) {
            String finishedMigrationFileName = config.afterRenameFileName.replace("migrating", migratedFilePostfix);
            sshConnection.renameFileName(config.afterRenameFileName, finishedMigrationFileName);
            if (config.error.exists() && config.error.length() != 0)
              sshConnection.uploadFile(config.error);
          }
        }
      }

      files = getFileNameList(migrationFilePattern, sshConfigBeanGetter.get());
    }

    String durationDateFormat = DateUtils.getTimeDifferenceStringFormat(new Date().getTime(), started.getTime());
    logger.info("##########################################################");
    logger.info("MIGRATION FINISHED ");
    logger.info("TOTAL MIGRATION DURATION: " + durationDateFormat);
    logger.info("##########################################################");


    /*
      FIXME: 3/16/18 если в промежутке между isMigrationGoingOn.set(true); и этим местом вылетит exception,
      FIXME:          то миграцию без перезапуска сервера никак не включишь */

    isMigrationGoingOn.set(false);
  }


  private MigrationConfig initMigrationConfig(String fileName, IdGenerator idGenerator) throws Exception {


    MigrationConfig config = new MigrationConfig();
    config.idGenerator = idGenerator;
    config.id = idGenerator.newId();
    config.originalFileName = fileName;

    logger.info("FILENAME: " + config.originalFileName);
    logger.info("ID: " + config.id);

    String tempFileName = Modules.dbDir() + "/build/migration/" + fileName;

    File copiedFile = new File(tempFileName);
    //noinspection ResultOfMethodCallIgnored
    copiedFile.getParentFile().mkdirs();

    try (SSHConnection sshConnection = new SSHConnection(sshConfigBeanGetter.get())) {

      if (sshConnection.isFileExist(fileName)) {

        config.afterRenameFileName = fileName + ".migrating" + config.id;
        sshConnection.renameFileName(fileName, config.afterRenameFileName);

        if (sshConnection.isFileExist(config.afterRenameFileName)) {
          logger.info("copying file");
          try (OutputStream out = new FileOutputStream(copiedFile)) {
            sshConnection.downloadFile(config.afterRenameFileName, out);
          }
        } else {
          logger.info("file " + config.afterRenameFileName + " no longer exist after renamed");
          return null;
        }
      } else {
        logger.info("file " + fileName + " not found in ssh directory");
        return null;
      }
    }

    String decompressedFilePath = copiedFile.getPath().replaceAll(".bz2", "");
    File decompressedFile = new File(decompressedFilePath);
    logger.info("decompressing file");
    FileUtils.decompressFile(copiedFile, decompressedFile);
    if (!decompressedFile.exists()) {
      logger.info("cant decompress file " + fileName);
      return null;
    }

    if (!copiedFile.delete()) {
      logger.info("could not delete file " + copiedFile.getPath());
    }


    logger.info("untaring file");
    String untarred = decompressedFilePath.replaceAll(".tar", "");
    File untareedFile = new File(untarred);
    FileUtils.untarFile(decompressedFile, untareedFile);
    if (!untareedFile.exists()) {
      logger.info("cant untar file");
      return null;
    }

    if (!decompressedFile.delete()) {
      logger.info("could not delete file " + decompressedFile.getPath());
    }

    config.toMigrate = untareedFile;

    String date = DateUtils.getDateWithTimeString(new Date());
    config.error = new File(Modules.dbDir() + "/build/migration/" +
      fileName + date + "migId-" + config.id + ".error");

    return config;
  }
}
