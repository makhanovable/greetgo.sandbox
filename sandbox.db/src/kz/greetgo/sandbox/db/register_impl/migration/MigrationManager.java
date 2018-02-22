package kz.greetgo.sandbox.db.register_impl.migration;

import com.jcraft.jsch.ChannelSftp;
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

@Bean
public class MigrationManager extends SSHWorker {

  public BeanGetter<MigrationController> controller;
  public BeanGetter<MigrationConfig> config;

  private Logger logger = Logger.getLogger(MigrationManager.class);

  public boolean connectAndMigrateOneCiaFile() {
    try {
      prepareSession(config.get().ciaUser(), config.get().ciaHost(), config.get().ciaPassword(), config.get().ciaPort());
      connect();

      String remoteCiaToSendDir = config.get().ciaPathToSend();
      channelSftp.cd(remoteCiaToSendDir);

      boolean ciaFileExists = false;
      Vector fileList = channelSftp.ls(remoteCiaToSendDir);
      // TODO:  заменить проверки на ls с параметрами, для прихода файлов с уже известными расширениями xml.tar.bz2?
      if (!fileList.isEmpty()) {
        for (Object o : fileList) {
          ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
          String remoteArchiveFilenameWithExt = entry.getFilename();

          if (remoteArchiveFilenameWithExt.startsWith("from_cia_")) {
            String remoteArchivedFileExt = "xml.tar.bz2";

            if (remoteArchiveFilenameWithExt.endsWith("." + remoteArchivedFileExt)) {
              String ciaFilename = remoteArchiveFilenameWithExt
                .substring(0, remoteArchiveFilenameWithExt.lastIndexOf(remoteArchivedFileExt) - 1);
              if (FilenameUtils.getExtension(ciaFilename).isEmpty()) {
                ciaFileExists = true;

                String rndId = Util.generateRandomString(8);
                String remoteTempArchiveFilenameWithExt = ciaFilename + "." + rndId + "." + remoteArchivedFileExt;
                String localErrorFilenameWithExt = ciaFilename + "." + rndId + ".error.txt";
                String localErrorFilepath = config.get().localTempDir() + "/" + localErrorFilenameWithExt;
                String localArchiveFilepath = config.get().localTempDir() + "/" + remoteTempArchiveFilenameWithExt;

                File localArchiveFile = new File(localArchiveFilepath);
                localArchiveFile.getParentFile().mkdirs();
                File localErrorFile = new File(localErrorFilepath);

                channelSftp.rename(remoteArchiveFilenameWithExt, remoteTempArchiveFilenameWithExt);

                // TODO: зачем нужна проверка файла после переименования, если генерируется уникальный ID файла в его названии?
                if (!exists(remoteTempArchiveFilenameWithExt))
                  continue;

                download(remoteTempArchiveFilenameWithExt, localArchiveFile);

                BZip2CompressorInputStream bz2is =
                  new BZip2CompressorInputStream(new FileInputStream(localArchiveFile));
                TarArchiveInputStream tarin = new TarArchiveInputStream(bz2is);
                ArchiveEntry archiveEntry;

                while (null != (archiveEntry = tarin.getNextEntry())) {
                  if (archiveEntry.getSize() < 1 || archiveEntry.isDirectory())
                    continue;

                  if (controller.get().migrateOneCiaFile(tarin, ciaFilename, localErrorFile)) {
                    channelSftp.rename(remoteCiaToSendDir + "/" + remoteTempArchiveFilenameWithExt,
                      config.get().ciaPathSent() + "/" + remoteTempArchiveFilenameWithExt);

                    String errorArchiveFilenameWithExt = localErrorFilenameWithExt + ".tar.bz2";
                    String localErrorArchiveFilepath = config.get().localTempDir() + "/" + errorArchiveFilenameWithExt;
                    File localErrorArchiveFile = new File(localErrorArchiveFilepath);

                    ArchiveUtil.compressFile_tar_bzip2(localErrorFile, localErrorArchiveFile);
                    upload(localErrorArchiveFile, errorArchiveFilenameWithExt);
                    channelSftp.rename(remoteCiaToSendDir + "/" + errorArchiveFilenameWithExt,
                      config.get().ciaPathSent() + "/" + errorArchiveFilenameWithExt);

                    break;
                  }
                }

                tarin.close();

                break;
              }
            }
          }
        }
      }

      if (!ciaFileExists) {
        logger.error("В директории отсутствует любой CIA файл");
        return false;
      }

      disconnect();
    } catch (Exception e) {
      disconnect();
      logger.warn("Ошибка при попытке удаленной миграции CIA файла на машине "
        + config.get().ciaUser() + "@" + config.get().ciaHost());
    }

    return true;
  }

  public boolean connectAndMigrateOneFrsFile() {
    try {
      prepareSession(config.get().frsUser(), config.get().frsHost(), config.get().frsPassword(), config.get().frsPort());
      connect();

      String remoteFrsToSendDir = config.get().frsPathToSend();
      channelSftp.cd(remoteFrsToSendDir);

      boolean frsFileExists = false;
      Vector fileList = channelSftp.ls(remoteFrsToSendDir);
      // TODO:  заменить проверки на ls с параметрами, для прихода файлов с уже известными расширениями xml.tar.bz2?
      if (!fileList.isEmpty()) {
        for (Object o : fileList) {
          ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
          String remoteArchiveFilenameWithExt = entry.getFilename();

          if (remoteArchiveFilenameWithExt.startsWith("from_frs_")) {
            String remoteArchivedFileExt = "json_row.txt.tar.bz2";

            if (remoteArchiveFilenameWithExt.endsWith("." + remoteArchivedFileExt)) {
              String frsFilename = remoteArchiveFilenameWithExt
                .substring(0, remoteArchiveFilenameWithExt.lastIndexOf(remoteArchivedFileExt) - 1);
              if (FilenameUtils.getExtension(frsFilename).isEmpty()) {
                frsFileExists = true;

                String rndId = Util.generateRandomString(8);
                String remoteTempArchiveFilenameWithExt = frsFilename + "." + rndId + "." + remoteArchivedFileExt;
                String localErrorFilenameWithExt = frsFilename + "." + rndId + ".error.txt";
                String localErrorFilepath = config.get().localTempDir() + "/" + localErrorFilenameWithExt;
                String localArchiveFilepath = config.get().localTempDir() + "/" + remoteTempArchiveFilenameWithExt;

                File localArchiveFile = new File(localArchiveFilepath);
                localArchiveFile.getParentFile().mkdirs();
                File localErrorFile = new File(localErrorFilepath);

                channelSftp.rename(remoteArchiveFilenameWithExt, remoteTempArchiveFilenameWithExt);

                // TODO: зачем нужна проверка файла после переименования, если генерируется уникальный ID файла в его названии?
                if (!exists(remoteTempArchiveFilenameWithExt))
                  continue;

                download(remoteTempArchiveFilenameWithExt, localArchiveFile);

                BZip2CompressorInputStream bz2is =
                  new BZip2CompressorInputStream(new FileInputStream(localArchiveFile));
                TarArchiveInputStream tarin = new TarArchiveInputStream(bz2is);
                ArchiveEntry archiveEntry;

                while (null != (archiveEntry = tarin.getNextEntry())) {
                  if (archiveEntry.getSize() < 1 || archiveEntry.isDirectory())
                    continue;

                  if (controller.get().migrateOneFrsFile(tarin, frsFilename, localErrorFile)) {
                    channelSftp.rename(remoteFrsToSendDir + "/" + remoteTempArchiveFilenameWithExt,
                      config.get().frsPathSent() + "/" + remoteTempArchiveFilenameWithExt);

                    String errorArchiveFilenameWithExt = localErrorFilenameWithExt + ".tar.bz2";
                    String localErrorArchiveFilepath = config.get().localTempDir() + "/" + errorArchiveFilenameWithExt;
                    File localErrorArchiveFile = new File(localErrorArchiveFilepath);

                    ArchiveUtil.compressFile_tar_bzip2(localErrorFile, localErrorArchiveFile);
                    upload(localErrorArchiveFile, errorArchiveFilenameWithExt);
                    channelSftp.rename(remoteFrsToSendDir + "/" + errorArchiveFilenameWithExt,
                      config.get().frsPathSent() + "/" + errorArchiveFilenameWithExt);

                    break;
                  }
                }

                tarin.close();

                break;
              }
            }
          }
        }
      }

      if (!frsFileExists) {
        logger.warn("В директории отсутствует любой FRS файл");
        return false;
      }

      disconnect();
    } catch (Exception e) {
      disconnect();
      logger.error("Ошибка при попытке удаленной миграции FRS файла на машине "
        + config.get().frsUser() + "@" + config.get().frsHost());
    }

    return true;
  }
}
