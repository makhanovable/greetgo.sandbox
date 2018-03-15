package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;

import java.util.List;

/**
 * Аутентификация, авторизация и работа с сессией
 */
public interface AuthRegister {
  /**
   * Сохраняет значение пользовательского параметра. У каждого пользователя свои значения
   *
   * @param personId идентификатор пользователя
   * @param name     имя параметра
   * @param value    значение параметра
   */
  void saveParam(String personId, UserParamName name, String value);

  /**
   * Возвращает значение указанного параметра
   *
   * @param personId идентификатор пользователя
   * @param name     имя указываемого параметра
   * @return значение
   */
  String getParam(String personId, UserParamName name);

  /**
   * Производит аутентификацию
   *
   * @param accountName логин
   * @param password    пароль
   * @return токен
   */
  String login(String accountName, String password);

  /**
   * Проверяет токен и сохраняет параметры сессии в ThreadLocal переменной, для дальнейшего использования в запросе
   *
   * @param token токен сессии
   * @throws SecurityError генерируется в случае нарушения секурити
   */
  void checkTokenAndPutToThreadLocal(String token);

  /**
   * Очищает параметры сессии из переменной ThreadLocal
   */
  void cleanTokenThreadLocal();

  /**
   * @return получает SessionInfo из ThreadLocal
   */
  SessionInfo getSessionInfo();

  /**
   * Предоставляет клиенту информацию о проделаной аутентификации
   *
   * @param personId идентификатор пользователя
   * @return информация о проделаной аутентификации
   */
  AuthInfo getAuthInfo(String personId);

  /**
   * Предоставляет детальную информацию о пользователе
   *
   * @param personId идентификатор пользователя
   * @return детальная информация о пользователе
   */
  UserInfo getUserInfo(String personId);
  List<UserInfo> getAllUserInfo();
  List<PrintedClientInfo> getClientsInfo();
}
