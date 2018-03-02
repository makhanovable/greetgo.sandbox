package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к ssh директории")
public interface SshConfig {

  @Description("host: address of remote server")
  @DefaultStrValue("127.0.0.1")
  String host();

  @Description("port which ssh daemon listening on remove server")
  @DefaultStrValue("22")
  int port();

  @Description("user of system")
  @DefaultStrValue("Some_User")
  String username();

  @Description("password")
  @DefaultStrValue("Secret")
  String password();

  @Description("directory where files for migration located")
  @DefaultStrValue("/var/metodology/")
  String migrationDir();


}
