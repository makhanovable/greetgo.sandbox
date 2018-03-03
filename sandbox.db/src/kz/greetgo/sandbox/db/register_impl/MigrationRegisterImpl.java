package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;

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

      MigrationConfig config = initConfig(fileName);

      if (config != null) {
//        Migration.getMigrationInstance(config).migrate();

      } else {
        System.out.println("somethink went wrong");
      }

      files = getFileNameList(migrationFilePattern);
    }


    isMigrationGoingOn = false;
  }

  private MigrationConfig initConfig(String fileName) throws Exception {

    MigrationConfig config = new MigrationConfig();

    config.id = idGenerator.get().newId();
    config.originalFileName = fileName;
    config.ready = false;
    String tempFileName = Modules.dbDir() + "/build/migration/" + fileName;
    File copiedFile = new File(tempFileName);
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

    copiedFile.delete();
    decompressedFile.delete();
    config.toMigrate = untareedFile;

    config.dbConfig = allConfigFactory.get().createPostgresDbConfig();

    config.ready = true;
    return config;
  }

  private void untarFile(File file, File dest) throws IOException {
    file.getParentFile().mkdirs();
    file.mkdir();

    try (FileInputStream fis = new FileInputStream(file);
         TarArchiveInputStream tis = new TarArchiveInputStream(fis)) {

      TarArchiveEntry tarEntry = null;

      while ((tarEntry = tis.getNextTarEntry()) != null) {

        if (!tarEntry.isDirectory()) {
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
