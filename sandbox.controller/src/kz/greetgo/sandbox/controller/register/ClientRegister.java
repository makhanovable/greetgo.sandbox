package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;

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
   * Возвращает клиентскую запись после сохранения или изменения клиентских деталей
   *
   * @param detailsToSave клиентские детали
   * @return клиентская запись
   */
  ClientRecord save(ClientDetailsToSave detailsToSave);
}
