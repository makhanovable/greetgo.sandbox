package kz.greetgo.sandbox.controller.register;

public interface TempSessionRegister {
  /**
   * Сохраняет информацию о предстоящей сессии для загрузки отчета и возвращает токен
   *
   * @param personId     идентификатор пользователя
   * @param urlPath      адресный путь скачиваемого файла\
   * @param lifetimeSecs срок жизни рабочей ссылки в секундах
   * @return сгенерированный токен
   */
  String save(String personId, String urlPath, long lifetimeSecs);

  /**
   * Проверяет валидность текущей сессии
   *
   * @param token   токен
   * @param urlPath адресный путь скачиваемого файла
   */
  void checkForValidity(String token, String urlPath);

  /**
   * Удаляет сессию и возвращает идентификатор пользователя
   *
   * @param token токен
   * @return идентификатор пользователя
   */
  String remove(String token);
}
