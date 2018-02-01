package kz.greetgo.sandbox.controller.register;

import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.*;

import java.io.OutputStream;
import java.util.List;

public interface ClientRegister {
  /**
   * Предоставляет количество страниц клиентских записей
   *
   * @param request принимаемые параметры страницы, сортировки и фильтрации в виде модели
   * @return количество страниц
   */
  long getCount(ClientRecordRequest request);

  /**
   * Предоставляет список клиентских записей
   *
   * @param listRequest принимаемые параметры страницы, сортировки и фильтрации в виде модели
   * @return список клиентских записей
   */
  List<ClientRecord> getRecordList(ClientRecordRequest listRequest);

  /**
   * Удаление клиентской записи
   *
   * @param id идентификатор клиентской записи
   */
  void removeRecord(long id);

  /**
   * Возвращает клиентские детали
   *
   * @param id идентификатор клиентской записи
   * @return клиентские детали одного клиента
   */
  ClientDetails getDetails(Long id);

  /**
   * Сохранение новых или изменение существующих клиентских деталей
   *
   * @param detailsToSave клиентские детали
   */
  void saveDetails(ClientDetailsToSave detailsToSave);

  /**
   * Подготавливает временную сессию для загрузки файла и возвращает токен сессии
   *
   * @param personId идентификатор пользователя
   * @return токен
   */
  String prepareRecordListStream(String personId);

  /**
   * Возвращает полный список записей в выходной поток
   *
   * @param outStream       выходной поток
   * @param request         принимаемые параметры страницы, сортировки и фильтрации в виде модели
   * @param fileContentType тип контента для формирования файла
   */
  void streamRecordList(String token, OutputStream outStream, ClientRecordRequest request, FileContentType fileContentType,
                        RequestTunnel requestTunnel) throws Exception;
}
