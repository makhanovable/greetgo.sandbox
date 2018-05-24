package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к БД (используется только БД Postgresql)")
public interface DbConfig {

  @Description("URL доступа к БД")
  @DefaultStrValue("jdbc:postgres:host:5432/sandbox")
  String url();

  @Description("Пользователь для доступа к БД")
  @DefaultStrValue("nkabylbai")
  String username();

  @Description("Пароль для доступа к БД")
  @DefaultStrValue("nkabylbai")
  String password();
}
