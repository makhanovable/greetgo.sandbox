package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class DataUtil {

    private final static String[] FOLDERS = new String[]{"/var/metodology/100_000/"};
    private final static String USER = "makhan";
    private final static String PASSWORD = "arduino121232";
    private final static String HOST = "localhost";
    private final static int PORT = 2220;

    private static Session session;
    private static ChannelSftp channelSftp;
    private static Channel channel;

    public static List<String> downloadFilesAndExtract() throws Exception {
        List<String> extractedFiles = new ArrayList<>();
        createConnection();

        for (String FOLDER : FOLDERS) {
            channelSftp.cd(FOLDER);
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(FOLDER);
            for (ChannelSftp.LsEntry oListItem : files)
                if (!oListItem.getAttrs().isDir()) {
                    String filename = oListItem.getFilename();
                    if (filename.endsWith("from_cia_2018-02-21-154532-1-300.xml.tar.bz2") ||
                            filename.endsWith("from_frs_2018-02-21-154543-1-30009.json_row.txt.tar.bz2")) {
                        String extractedFilePath = sendCommand("tar -jxvf " + FOLDER.substring(1, FOLDER.length()) + oListItem.getFilename());
                        System.out.println(extractedFilePath);
                    }
                }
        }
        closeConnection();
        return extractedFiles;
    }

    private static void downloadFile(String file) throws Exception {
        ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.cd("/");
        channelSftp.get(file);
    }

    private static void createConnection() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(USER, HOST, PORT);
        session.setPassword(PASSWORD);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
    }

    private static String sendCommand(String command) throws Exception {
        StringBuilder outputBuffer = new StringBuilder();
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        InputStream commandOutput = channel.getInputStream();
        channel.connect();
        int readByte = commandOutput.read();
        while (readByte != 0xffffffff) {
            outputBuffer.append((char) readByte);
            readByte = commandOutput.read();
        }
        channel.disconnect();
        return outputBuffer.toString();
    }

    private static void closeConnection() {
        channelSftp.exit();
        session.disconnect();
        channel.disconnect();
    }

}
