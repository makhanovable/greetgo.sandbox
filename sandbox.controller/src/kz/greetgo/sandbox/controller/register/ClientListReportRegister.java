package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;

import java.io.OutputStream;

public interface ClientListReportRegister {

  /**
   * Сохраняет информацию о предстоящей сессии для загрузки отчета и возвращает идентификатор отчета
   *
   * @param personId        идентификатор пользователя
   * @param request         запрос для фильтрации и сортировки
   * @param fileContentType тип файла
   * @return сгенерированный идентификатор отчета
   */
  String save(String personId, ClientRecordRequest request, FileContentType fileContentType) throws Exception;

  /**
   * Проверяет валидность загрузки отчета, yдаляет инстанцию и возвращает идентификатор пользователя
   *
   * @param reportIdInstance идентификатор отчета
   * @return идентификатор пользователя
   */
  ClientListReportInstance checkForValidity(String reportIdInstance) throws Exception;

  void generate(OutputStream outputStream, String personId, ClientRecordRequest request,
                FileContentType fileContentType);
}
