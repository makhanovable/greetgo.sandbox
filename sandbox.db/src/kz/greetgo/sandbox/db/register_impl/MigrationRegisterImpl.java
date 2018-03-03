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
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
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
  @SuppressWarnings("WeakerAccess")
  public BeanGetter<JdbcSandbox> jdbcSandbox;


  private AtomicBoolean isMigrationGoingOn = new AtomicBoolean(false);

  final Logger logger = Logger.getLogger(getClass());

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
        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(
          dbConfig.url(),
          dbConfig.username(),
          dbConfig.password())) {

          Migration.getMigrationInstance(config).migrate(connection);
        }

      } else {
        System.out.println("somethink went wrong");
      }

      files = getFileNameList(migrationFilePattern);
    }


    isMigrationGoingOn.set(false);
  }

  private MigrationConfig initConfig(String fileName) throws Exception {

    MigrationConfig config = new MigrationConfig();

    config.id = idGenerator.get().newId();
    config.originalFileName = fileName;
    config.ready = false;
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
          return null;
        }
      } else {
        return null;
      }
    }

    String decompressed = copiedFile.getPath().replaceAll(".bz2", "");
    File decompressedFile = new File(decompressed);
    decompressFile(copiedFile, decompressedFile);
    if (!decompressedFile.exists())
      return null;

    String untareed = decompressed.replaceAll(".tar", "");
    File untareedFile = new File(untareed);
    untarFile(decompressedFile, untareedFile);
    if (!untareedFile.exists())
      return null;

    //noinspection ResultOfMethodCallIgnored
    copiedFile.delete();
    //noinspection ResultOfMethodCallIgnored
    decompressedFile.delete();
    config.toMigrate = untareedFile;

    config.ready = true;
    return config;
  }

  private void untarFile(File file, File dest) throws IOException {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    //noinspection ResultOfMethodCallIgnored
    file.mkdir();

    try (FileInputStream fis = new FileInputStream(file);
         TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {

      TarArchiveEntry tarEntry;

      while ((tarEntry = tis.getNextTarEntry()) != null) {

        if (!tarEntry.isDirectory()) {
          //noinspection ResultOfMethodCallIgnored
          dest.getParentFile().mkdirs();
          try (FileOutputStream fos = new FileOutputStream(dest)) {
            IOUtils.copy(tis, fos);
//          ServerUtil.copyStreamsAndCloseIn(tis, fos);
            break;
          }
        }
      }
    }
  }


  private void decompressFile(File file, File dest) throws Exception {

    try (
      FileInputStream in = new FileInputStream(file);
      BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
      OutputStream out = new FileOutputStream(dest)) {

      IOUtils.copy(bzIn, out);
//      ServerUtil.copyStreamsAndCloseIn(bzIn, out);
    }
  }

  private List<String> getFileNameList(Pattern pattern) throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(allConfigFactory.get().createSshConfig())) {
      files = sshConnection.getFileNameList(".");
    }
    return files.stream().filter(o -> pattern.matcher(o).matches()).collect(Collectors.toList());
  }


}
