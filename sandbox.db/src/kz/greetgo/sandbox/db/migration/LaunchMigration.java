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

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

    private static void extract(String dest) throws Exception {
//        unTar(dest, new File("build/o/"));
    }
//
//    private static void unTar(final String inputFile, final File outputDir) throws Exception {
//        // 1st InputStream from your compressed file
//        FileInputStream in = new FileInputStream(new File(inputFile));
//// wrap in a 2nd InputStream that deals with compression
//        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
//// wrap in a 3rd InputStream that deals with tar
//        TarArchiveInputStream tarIn = new TarArchiveInputStream(bzIn);
//        ArchiveEntry entry = null;
//
//        while (null != (entry = tarIn.getNextEntry())) {
//            if (entry.getSize() < 1) {
//                continue;
//            }
//            // use your parser here, the tar inputStream deals with the size of the current entry
//            parser.parse(tarIn);
//        }
//        tarIn.close();
//    }


}