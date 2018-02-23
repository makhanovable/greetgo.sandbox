package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к ssh директории")
public interface SshConfig {

  @Description("host")
  @DefaultStrValue("127.0.0.1")
  String host();

  @Description("user of system")
  @DefaultStrValue("Some_User")
  String username();

  @Description("password")
  @DefaultStrValue("Secret")
  String password();
}
