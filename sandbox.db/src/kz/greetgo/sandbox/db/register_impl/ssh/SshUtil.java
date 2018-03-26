package kz.greetgo.sandbox.db.register_impl.ssh;

import kz.greetgo.sandbox.db.configs.SshConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SshUtil {
  public static List<String> getFileNameList(Pattern pattern, SshConfig sshConfig) throws Exception {
    List<String> files;
    try (SSHConnection sshConnection = new SSHConnection(sshConfig)) {
      files = sshConnection.getFileNameList(".");
    }
    if (files == null)
      return new ArrayList<>();

    return files.stream().filter(filename -> pattern.matcher(filename).matches()).collect(Collectors.toList());
  }
}
