package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DataUtil {

    private final static String[] FOLDERS = new String[]{"/var/metodology/100_000/build/out_files/"};
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
                // TODO чередовать cia и frs
                if (!oListItem.getAttrs().isDir()) {
                    String dest = "build" + FOLDER + oListItem.getFilename(); // TODO all files needed
                    if (dest.endsWith("xml") ||
                            dest.endsWith("txt")) {
                        channelSftp.get(oListItem.getFilename(), dest);
                       // sendCommand("cmd tar -jxvf " + oListItem.getFilename());
                        extractedFiles.add(dest);
                    }
                }
        }
        closeConnection();
        return extractedFiles;
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

    private static void sendCommand(String command) throws Exception {
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
        System.out.println(command);
        channel.disconnect();
        System.out.println(outputBuffer.toString());
    }

    private static void closeConnection() {
        channelSftp.exit();
        session.disconnect();
        channel.disconnect();
    }

}
