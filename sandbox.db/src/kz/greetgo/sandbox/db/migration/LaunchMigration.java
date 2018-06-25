package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

public class LaunchMigration {

    public static void main(String args[]) throws Exception {
        final File file = new File("build/__migration__");
        file.getParentFile().mkdirs();
        file.createNewFile();

        System.out.println("To stop next migration portion delete file " + file);
        System.out.println("To stop next migration portion delete file " + file);
        System.out.println("To stop next migration portion delete file " + file);

        downloadFiles();
    }


    private static void downloadFiles() {

        String[] FOLDERS = new String[]{"/var/metodology/100_000/", "/var/metodology/1_000_000/"};

        String user = "makhan";
        String password = "arduino121232";
        String host = "localhost";
        int port = 2220;

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            for (String FOLDER : FOLDERS) {
                channelSftp.cd(FOLDER);
                Vector<ChannelSftp.LsEntry> filelist = channelSftp.ls(FOLDER);
                for (ChannelSftp.LsEntry oListItem : filelist) {
                    if (!oListItem.getAttrs().isDir()) {
                        System.out.println("Downloading " + oListItem.getFilename());
                        String dest = "build" + FOLDER + oListItem.getFilename();
                        channelSftp.get(oListItem.getFilename(), dest);
                        if (dest.endsWith("tar.bz2"))
                            extract(dest);
                    }
                }
            }

            channelSftp.exit();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String tarFileName;
    private static File dest;

    private static void extract(String det) throws Exception {
        tarFileName = det;
        dest = new File("build/oo/");
        untar();
    }

    private static InputStream getDecompressedInputStream(final String name, final InputStream istream) throws IOException {
        System.out.println("untar: decompress " + name + " to " + dest);
        if (name == null) {
            throw new RuntimeException("fileName to decompress can not be null");
        }
        if (name.toLowerCase().endsWith("gzip") || name.toLowerCase().endsWith("gz")) {
            return new BufferedInputStream(new GZIPInputStream(istream));
        } else if (name.toLowerCase().endsWith("bz2") || name.toLowerCase().endsWith("bzip2")) {
            final char[] magic = new char[]{'B', 'Z'};
            for (int i = 0; i < magic.length; i++) {
                if (istream.read() != magic[i]) {
                    throw new RuntimeException("Invalid bz2 file." + name);
                }
            }
            return new BufferedInputStream(new CBZip2InputStream(istream));
        } else if (name.toLowerCase().endsWith("tar")) {
            return istream;
        }
        throw new RuntimeException("can only detect compression for extension tar, gzip, gz, bz2, or bzip2");
    }

    public static void untar() throws IOException {
        System.out.println("untar: untar " + tarFileName + " to " + dest);
        TarInputStream tin = null;
        try {
            if (!dest.exists()) {
                dest.mkdir();
            }
            tin = new TarInputStream(getDecompressedInputStream(tarFileName, new FileInputStream(new File(tarFileName))));
            TarEntry tarEntry = tin.getNextEntry();

            while (tarEntry != null) {
                File destPath = new File(dest.toString() + File.separatorChar + tarEntry.getName());

                if (tarEntry.isDirectory()) {
                    destPath.mkdir();
                } else {
                    if (!destPath.getParentFile().exists()) {
                        destPath.getParentFile().mkdirs();
                    }
                    System.out.println("untar: untar " + tarEntry.getName() + " to " + destPath);
                    FileOutputStream fout = new FileOutputStream(destPath);
                    try {
                        tin.copyEntryContents(fout);
                    } finally {
                        fout.flush();
                        fout.close();
                    }
                }
                tarEntry = tin.getNextEntry();
            }
        } finally {
            if (tin != null) {
                tin.close();
            }
        }
    }
}
