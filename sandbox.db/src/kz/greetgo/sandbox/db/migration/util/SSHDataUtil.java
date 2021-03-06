package kz.greetgo.sandbox.db.migration.util;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

public class SSHDataUtil {

    private final static String[] FOLDERS = new String[]{"/var/metodology/100_000/", "/var/metodology/1_000_000/"};
    private final static String USER = "makhan";
    private final static String PASSWORD = "arduino121232";
    private final static String HOST = "localhost";
    private final static int PORT = 2220;

    private static Session session;
    private static ChannelSftp channelSftp;
    private static Channel channel;

    public static ArrayList<String> downloadFilesAndExtract() throws Exception {
        ArrayList<String> xml = new ArrayList<>();
        ArrayList<String> json = new ArrayList<>();

        // LOCAL Files
        xml.add("out_files/from_cia_2018-02-21-154929-1-300.xml");
        json.add("out_files/from_frs_2018-02-21-155112-1-30002.json_row.txt");
//        xml.add("out_files/from_cia_2018-02-21-154535-4-100000.xml");
//        json.add("out_files/from_frs_2018-02-21-154551-3-1000004.json_row.txt");
//        xml.add("out_files/from_cia_2018-02-21-154955-5-1000000.xml");
//        json.add("out_files/from_frs_2018-02-21-155121-3-10000007.json_row.txt");

//        to downloading files from server define USER PASSWORD HOST PORT
//        and uncomment this
//        createConnection();
//
//        for (String FOLDER : FOLDERS) {
//            channelSftp.cd(FOLDER);
//            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(FOLDER);
//            for (ChannelSftp.LsEntry oListItem : files)
//                if (!oListItem.getAttrs().isDir()) {
//                    String filename = oListItem.getFilename();
//                    if (filename.endsWith("tar.bz2")) {
//                        String extractedFilePath = sendCommand("tar -jxvf "
//                                + FOLDER.substring(1, FOLDER.length()) + oListItem.getFilename());
//                        extractedFilePath = extractedFilePath.replace("\n", "");
//                        if (extractedFilePath.endsWith("txt"))
//                            json.add(extractedFilePath);
//                        else xml.add(extractedFilePath);
//                    }
//                }
//        }
//        downloadFiles();
//        closeConnection();

        return merge(xml, json);
    }

    private static void downloadFiles() throws Exception {
        String folder = "/out_files";
        channelSftp.cd(folder);
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(folder);
        for (ChannelSftp.LsEntry oListItem : files)
            if (!oListItem.getAttrs().isDir()) {
                String filename = oListItem.getFilename();
                if (filename.endsWith("xml") ||
                        filename.endsWith("txt")) {
                    channelSftp.get(filename, folder);
                }
            }
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

    private static ArrayList merge(ArrayList<String> a, ArrayList<String> b) {
        int c1 = 0, c2 = 0;
        ArrayList<String> res = new ArrayList<>();
        while (c1 < a.size() || c2 < b.size()) {
            if (c1 < a.size()) res.add((String) a.get(c1++));
            if (c2 < b.size()) res.add((String) b.get(c2++));
        }
        return res;
    }

    private static void closeConnection() {
        channelSftp.exit();
        session.disconnect();
        channel.disconnect();
    }

}
