package kz.greetgo.sandbox.db.register_impl.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.util.ServerUtil;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;


public class SSHConnection implements Closeable {

  private Session session;
  private ChannelSftp channel;
  private SshConfig sshConfig;

  public SSHConnection(SshConfig sshConfig) throws Exception {
    this.sshConfig = sshConfig;
    open();
  }

  private void open() throws Exception {
    JSch jsch = new JSch();
//    sshConfig.port();
    session = jsch.getSession(sshConfig.username(), sshConfig.host(), sshConfig.port());
    session.setPassword(sshConfig.password());
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);

    session.connect();
    channel = (ChannelSftp) session.openChannel("sftp");
    channel.connect();
    channel.cd(sshConfig.migrationDir());
  }

  public List<String> getFileNameList(String path) throws SftpException {
    Vector<ChannelSftp.LsEntry> entries = channel.ls(path);
    return entries.stream().map(o -> o.getFilename()).collect(Collectors.toList());
//    ((ChannelExec) (Channel) channel).setCommand("ls -lct");
//    channel.run();
//    Vector filelist = channel.run().map(o -> o.getFilename()).collect(Collectors.toList());
  }

  public void renameFileName(String fileName, String renameTo) throws SftpException {
    channel.rename(fileName, renameTo);
  }

  public boolean isFileExist(String fileName) throws Exception {
    try {
      channel.lstat(fileName);
    } catch (SftpException e) {
      if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
        return false;
      } else {
        throw new Exception();
      }
    }
    return true;
  }

  public void downloadFile(String fileName, OutputStream out) throws Exception {
    try (InputStream in = channel.get(fileName);
         BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in)) {

      ServerUtil.copyStreamsAndCloseIn(bzIn, out);
    }
  }

  @Override
  public void close() throws IOException {
    channel.disconnect();
    session.disconnect();
  }
}
