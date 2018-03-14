package kz.greetgo.sandbox.db.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

  public static void untarFile(File file, File dest) throws IOException {
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

  public static void decompressFile(File file, File dest) throws Exception {

    try (
      FileInputStream in = new FileInputStream(file);
      BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
      OutputStream out = new FileOutputStream(dest)) {

      IOUtils.copy(bzIn, out);
//      ServerUtil.copyStreamsAndCloseIn(bzIn, out);
    }
  }
}
