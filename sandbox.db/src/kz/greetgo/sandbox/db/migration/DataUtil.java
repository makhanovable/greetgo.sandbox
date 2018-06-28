package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.*;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

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
                // TODO чередовать cia и frs
                if (!oListItem.getAttrs().isDir()) {
                    String dest = "build" + FOLDER + oListItem.getFilename(); // TODO all files needed
                    if (dest.endsWith("from_cia_2018-02-21-154532-1-300.xml.tar.bz2") ||
                            dest.endsWith("from_frs_2018-02-21-154543-1-30009.json_row.txt.tar.bz2")) {
                        channelSftp.get(oListItem.getFilename(), dest);
                       // sendCommand("cmd tar -jxvf " + oListItem.getFilename());
                        extractedFiles.add(extract(dest));
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
//        StringBuilder outputBuffer = new StringBuilder();
//        Channel channel = session.openChannel("exec");
//        ((ChannelExec) channel).setCommand(command);
//        InputStream commandOutput = channel.getInputStream();
//        channel.connect();
//        int readByte = commandOutput.read();
//        while (readByte != 0xffffffff) {
//            outputBuffer.append((char) readByte);
//            readByte = commandOutput.read();
//        }
//        System.out.println(command);
//        channel.disconnect();
//        System.out.println(outputBuffer.toString());
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();
        byte[] tmp = new byte[1024];
        while (true) {
            System.out.println(channel.isClosed());
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            Thread.sleep(1000);
        }
        channel.disconnect();
        System.out.println("DONE");

    }


    private static String extract(String tarFileName) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("starting extracting ....." + tarFileName);
        String path = null;
        File dest = new File("build/extracted/");
        TarInputStream tin;
        if (!dest.exists())
            dest.mkdir();
        tin = new TarInputStream(getDecompressedInputStream(new FileInputStream
                (new File(tarFileName))));
        TarEntry tarEntry = tin.getNextEntry();

        while (tarEntry != null) {
            File destPath = new File(dest.toString() + File.separatorChar + tarEntry.getName());
            path = destPath.getPath();
            if (tarEntry.isDirectory())
                destPath.mkdir();
            else {
                if (!destPath.getParentFile().exists())
                    destPath.getParentFile().mkdirs();
                FileOutputStream fout = new FileOutputStream(destPath);
                tin.copyEntryContents(fout);
                fout.flush();
                fout.close();
            }
            tarEntry = tin.getNextEntry();
        }
        tin.close();
        long end = System.currentTimeMillis();
        System.out.println("EXTRACTED TIME = " + (end - start));
        System.out.println();
        return path;
    }

    private static InputStream getDecompressedInputStream(final InputStream stream) throws Exception {
        final char[] magic = new char[]{'B', 'Z'};
        for (char aMagic : magic)
            if (stream.read() != aMagic)
                throw new RuntimeException("Invalid bz2 file");
        return new BufferedInputStream(new CBZip2InputStream(stream));
    }

    private static void closeConnection() {
        channelSftp.exit();
        session.disconnect();
        channel.disconnect();
    }

//    private void parseXmlAndInsertToTempTable(String extractedXmlPath) throws Exception {
//        System.out.println("starting parsing XML and inserting to tmp ......");
//        long start = System.currentTimeMillis();
//        SAXParserFactory spf = SAXParserFactory.newInstance();
//        SAXParser saxParser = spf.newSAXParser();
//        XMLReader xmlReader = saxParser.getXMLReader();
//
//        XmlSaxParser parser = new XmlSaxParser(this);
//
//        xmlReader.setContentHandler(parser);
//        xmlReader.parse(extractedXmlPath);
//        System.out.println("ending parsing XML and insering to tmp....");
//        long end = System.currentTimeMillis();
//        System.out.println("TIME TO PARSE and INSERT = " + (end - start));
//        System.out.println("INSERTED = " + parser.count + " XML");
//    }

    public static void main(String[] args) throws Exception {

        String command = "cmd ls -ltr";
        try {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            // Create a JSch session to connect to the server
            Session session = jsch.getSession(USER, HOST, PORT);
            session.setPassword(PASSWORD);
            session.setConfig(config);
            // Establish the connection
            session.connect();
            System.out.println("Connected...");

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("Exit Status: "
                            + channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
