package kz.greetgo.sandbox.db.register_impl.migration;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.register_impl.ssh.SSHWorker;
import kz.greetgo.sandbox.db.util.ArchiveUtil;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import static kz.greetgo.sandbox.db.register_impl.migration.MigrationManager.eFiletype.CIA;

@Bean
public class MigrationManager {

  public BeanGetter<MigrationController> controller;
  public BeanGetter<MigrationConfig> config;

  private Logger logger = Logger.getLogger(MigrationManager.class);

  public enum eFiletype {
    CIA,
    FRS
  }

  public boolean connectAndMigrateOneFile(eFiletype filetype) {
    String user, host, password;
    int port;
    String fileTypename = filetype.name();

    switch (filetype) {
      case CIA: {
        user = config.get().ciaUser();
        host = config.get().ciaHost();
        password = config.get().ciaPassword();
        port = config.get().ciaPort();
        break;
      }
      case FRS: {
        user = config.get().frsUser();
        host = config.get().frsHost();
        password = config.get().frsPassword();
        port = config.get().frsPort();
        break;
      }
      default: {
        throw new RuntimeException("Тип файла " + fileTypename + " для миграции не имплементирован");
      }
    }

    String sshHostname = SSHWorker.makeHostname(user, host);

    try (SSHWorker sshWorker = new SSHWorker()) {
      try {
        sshWorker.prepareSession(user, host, password, port);
      } catch (JSchException e) {
        logger.warn("Не удалось подготовить сессию для миграции " + fileTypename + " файла у " + sshHostname, e);

        return false;
      }

      sshWorker.connect();

      String remoteToSendDir;
      if (filetype == CIA)
        remoteToSendDir = config.get().ciaPathToSend();
      else
        remoteToSendDir = config.get().frsPathToSend();
      sshWorker.channelSftp.cd(remoteToSendDir);

      boolean remoteFileExists = false;
      Vector fileList = sshWorker.channelSftp.ls(remoteToSendDir);
      String localTempDir = config.get().localTempDir();
      // TODO:  заменить проверки на ls с параметрами, для прихода файлов с уже известными расширениями xml.tar.bz2?
      if (!fileList.isEmpty()) {
        for (Object o : fileList) {
          ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
          String remoteArchiveFilenameWithExt = entry.getFilename();

          String filenameStart;
          if (filetype == CIA)
            filenameStart = "from_cia_";
          else
            filenameStart = "from_frs_";

          if (remoteArchiveFilenameWithExt.startsWith(filenameStart)) {
            String remoteArchivedFileExt;
            if (filetype == CIA)
              remoteArchivedFileExt = "xml";
            else
              remoteArchivedFileExt = "json_row.txt";
            remoteArchivedFileExt += ".tar.bz2";

            if (remoteArchiveFilenameWithExt.endsWith("." + remoteArchivedFileExt)) {
              String filename = remoteArchiveFilenameWithExt
                .substring(0, remoteArchiveFilenameWithExt.lastIndexOf(remoteArchivedFileExt) - 1);
              if (FilenameUtils.getExtension(filename).isEmpty()) {
                String rndId = Util.generateRandomString(8);
                String remoteTempArchiveFilenameWithExt = filename + "." + rndId + "." + remoteArchivedFileExt;
                String localErrorFilenameWithExt = filename + "." + rndId + ".error.txt";
                String localErrorFilepath = localTempDir + "/" + localErrorFilenameWithExt;
                String localArchiveFilepath = localTempDir + "/" + remoteTempArchiveFilenameWithExt;

                File localArchiveFile = new File(localArchiveFilepath);
                localArchiveFile.getParentFile().mkdirs();
                File localErrorFile = new File(localErrorFilepath);

                sshWorker.channelSftp.rename(remoteArchiveFilenameWithExt, remoteTempArchiveFilenameWithExt);

                // TODO: зачем нужна проверка файла после переименования, если генерируется уникальный ID файла в его названии?
                if (!sshWorker.exists(remoteTempArchiveFilenameWithExt))
                  continue;

                sshWorker.download(remoteTempArchiveFilenameWithExt, localArchiveFile);

                try (BZip2CompressorInputStream bz2is =
                       new BZip2CompressorInputStream(new FileInputStream(localArchiveFile));
                     TarArchiveInputStream tarin = new TarArchiveInputStream(bz2is)) {
                  ArchiveEntry archiveEntry;

                  while (null != (archiveEntry = tarin.getNextEntry())) {
                    if (archiveEntry.getSize() < 1 || archiveEntry.isDirectory())
                      continue;

                    File localReportFile = new File(localTempDir + "/" + filename + "." + rndId + ".ods");

                    String remoteSentDir;
                    boolean migrationSuccess;
                    if (filetype == CIA) {
                      remoteSentDir = config.get().ciaPathSent();
                      migrationSuccess =
                        controller.get().migrateOneCiaFile(tarin, filename, localErrorFile, localReportFile);
                    } else {
                      remoteSentDir = config.get().frsPathSent();
                      migrationSuccess =
                        controller.get().migrateOneFrsFile(tarin, filename, localErrorFile, localReportFile);
                    }

                    if (migrationSuccess) {
                      sshWorker.channelSftp.rename(remoteToSendDir + "/" + remoteTempArchiveFilenameWithExt,
                        remoteSentDir + "/" + remoteTempArchiveFilenameWithExt);

                      String errorArchiveFilenameWithExt = localErrorFilenameWithExt + ".tar.bz2";
                      String localErrorArchiveFilepath = localTempDir + "/" + errorArchiveFilenameWithExt;
                      File localErrorArchiveFile = new File(localErrorArchiveFilepath);

                      ArchiveUtil.compressFile_tar_bzip2(localErrorFile, localErrorArchiveFile);
                      sshWorker.upload(localErrorArchiveFile, errorArchiveFilenameWithExt);
                      sshWorker.channelSftp.rename(remoteToSendDir + "/" + errorArchiveFilenameWithExt,
                        remoteSentDir + "/" + errorArchiveFilenameWithExt);

                      remoteFileExists = true;

                      break;
                    }
                  }
                }
              }
            }
          }

          if (remoteFileExists)
            break;
        }
      }

      if (!remoteFileExists) {
        logger.error("В директории " + remoteToSendDir + " отсутствует любой " + fileTypename + " файл");
        return false;
      }
    } catch (Exception e) {
      logger.error("Ошибка при попытке удаленной миграции " +
        config.get().frsPathToSend() + " файла на машине " + sshHostname, e);

      return false;
    }

    return true;
  }
}
