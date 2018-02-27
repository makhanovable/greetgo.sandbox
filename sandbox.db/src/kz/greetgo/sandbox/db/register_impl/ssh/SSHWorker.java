package kz.greetgo.sandbox.db.register_impl.ssh;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;

public class SSHWorker implements AutoCloseable {

  public Session session;
  public Channel channel;
  public ChannelSftp channelSftp;

  public void prepareSession(String username, String host, String password, int port) throws JSchException {
    JSch jsch = new JSch();

    session = jsch.getSession(username, host, port);
    session.setPassword(password);

    Properties properties = new Properties();
    properties.put("StrictHostKeyChecking", "no");

    session.setConfig(properties);
  }

  public void connect() throws JSchException {
    session.connect();
    channel = session.openChannel("sftp");
    channel.connect();
    channelSftp = (ChannelSftp) channel;
  }

  public void disconnect() {
    channel.disconnect();
    session.disconnect();
  }

  public void download(String remoteFilepath, File destFile) throws Exception {
    BufferedInputStream bis = new BufferedInputStream(channelSftp.get(remoteFilepath));
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));

    byte[] buffer = new byte[1024];
    int readCount;
    while ((readCount = bis.read(buffer)) > 0)
      bos.write(buffer, 0, readCount);

    bis.close();
    bos.close();
  }

  public void upload(File srcFile, String remoteFilename) throws Exception {
    channelSftp.put(new FileInputStream(srcFile), remoteFilename);
  }

  public boolean exists(String path) throws SftpException {
    Vector res;
    try {
      res = channelSftp.ls(path);
    } catch (SftpException e) {
      if (e.id == SSH_FX_NO_SUCH_FILE) {
        return false;
      }
      throw e;
    }
    return res != null && !res.isEmpty();
  }

  @Override
  public void close() {
    disconnect();
  }

  public static String makeHostname(String user, String host) {
    return user + "@" + host;
  }
}
