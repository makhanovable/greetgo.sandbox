package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры миграции с удаленных машин, где каждый тип файла может иметь отдельный источник")
public interface MigrationConfig {

  @Description("Локальная директория, куда сохраняются файлы")
  @DefaultStrValue("migration")
  String localTempDir();

  @Description("Пользователь машины, на которой хранятся CIA файлы")
  @DefaultStrValue("nsmagulov")
  String ciaUser();

  @Description("Имя хоста, на которой хранятся CIA файлы")
  @DefaultStrValue("localhost")
  String ciaHost();

  @Description("Порт машины с CIA файлами")
  @DefaultIntValue(22)
  int ciaPort();

  @Description("Пароль для пользователя с CIA файлами")
  @DefaultStrValue("111")
  String ciaPassword();

  @Description("Удаленная директория, в которой лежат CIA файлы")
  @DefaultStrValue("/home/nsmagulov/migration/to_send")
  String ciaPathToSend();

  @Description("Удаленная директория, в которой хранятся промигрированные CIA файлы")
  @DefaultStrValue("/home/nsmagulov/migration/sent")
  String ciaPathSent();

  @Description("Пользователь машины, на которой хранятся FRS файлы")
  @DefaultStrValue("nsmagulov")
  String frsUser();

  @Description("Имя хоста, на которой хранятся FRS файлы")
  @DefaultStrValue("localhost")
  String frsHost();

  @Description("Порт машины с FRS файлами")
  @DefaultIntValue(22)
  int frsPort();

  @Description("Пароль для пользователя с FRS файлами")
  @DefaultStrValue("111")
  String frsPassword();

  @Description("Удаленная директория, в которой лежат FRS файлы")
  @DefaultStrValue("/home/nsmagulov/migration/to_send")
  String frsPathToSend();

  @Description("Удаленная директория, в которой хранятся промигрированные FRS файлы")
  @DefaultStrValue("/home/nsmagulov/migration/sent")
  String frsPathSent();
}
