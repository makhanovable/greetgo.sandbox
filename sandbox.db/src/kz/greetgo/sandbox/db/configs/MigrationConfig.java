package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("???")
public interface MigrationConfig {

  @Description("???")
  @DefaultStrValue("localhost")
  String ciaHost();

  @Description("???")
  @DefaultIntValue(22)
  int ciaPort();

  @Description("???")
  @DefaultStrValue("111")
  String ciaPassword();

  @Description("???")
  @DefaultStrValue("/home/nsmagulov/migration/to_send")
  String ciaPathToSend();

  @Description("???")
  @DefaultStrValue("/home/nsmagulov/migration/sent")
  String ciaPathSent();


  @Description("???")
  @DefaultStrValue("localhost")
  String frsHost();

  @Description("???")
  @DefaultIntValue(22)
  int frsPort();

  @Description("???")
  @DefaultStrValue("111")
  String frsPassword();

  @Description("???")
  @DefaultStrValue("/home/nsmagulov/migration/to_send")
  String frsPathToSend();

  @Description("???")
  @DefaultStrValue("/home/nsmagulov/migration/sent")
  String frsPathSent();

}
