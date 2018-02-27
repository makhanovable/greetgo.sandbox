package kz.greetgo.sandbox.db.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

public class ArchiveUtil {
  public static void compressFile_tar_bzip2(File file, File compressedFile) throws IOException {
    FileOutputStream fos = new FileOutputStream(compressedFile);

    TarArchiveOutputStream taos =
      new TarArchiveOutputStream(new BZip2CompressorOutputStream(new BufferedOutputStream(fos)));
    //taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
    //taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

    taos.putArchiveEntry(new TarArchiveEntry(file, "/" + file.getName()));

    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    IOUtils.copy(bis, taos);
    taos.closeArchiveEntry();
    bis.close();

    taos.close();
    fos.close();
  }
}
