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
   * Сохраняет данные для загрузки файла из нового окна на клиенте
   *
   * @param personId        идентификатор персоны
   * @param request         запрос фильтрации и сортировки
   * @param fileContentType тип файлового контента
   * @return возвращает идентификатор инстанции отчета
   * @throws Exception
   */
  String prepareRecordListStream(String personId, ClientRecordRequest request, FileContentType fileContentType) throws Exception;

  /**
   * Выводит в поток отчет заранее запрошенный отчет
   *
   * @param reportIdInstance идентификатор отчета
   * @param outStream        выходной поток файла
   * @param requestTunnel    теннель контроллера
   * @throws Exception
   */
  void streamRecordList(String reportIdInstance, OutputStream outStream, RequestTunnel requestTunnel) throws Exception;
}
