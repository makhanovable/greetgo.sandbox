package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.Date;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @Mapping("/ping")
  public void ping(RequestTunnel requestTunnel) {


    requestTunnel.getResponseWriter().append("hello");
  }

  @ToJson
  @Mapping("/list")
  public List<ClientRecord> list(@Par("limit") int limit, @Par("page") int page, @Par("filter") String filter,
                                 @Par("orderBy") String orderBy, @Par("desc") int desc) {

    return clientRegister.get().getClientInfoList(limit, page, filter, orderBy, desc);
  }

  @ToJson
  @Mapping("/amount")
  public long getAmount(@Par("filter") String filter) {
    return this.clientRegister.get().getClientsSize(filter);
  }

  @ToJson
  @Mapping("/remove")
  public float removeClients(@Par("ids") @Json List<String> ids) {
    return this.clientRegister.get().remove(ids);
  }

  @ToJson
  @Mapping("/detail")
  public ClientDetail detail(@Par("id") String id) {
    return this.clientRegister.get().detail(id);
  }

  @ToJson
  @Mapping("/addOrUpdate")
  public void addOrUpdate(@Par("client") @Json ClientToSave clientToSave) {
    this.clientRegister.get().addOrUpdate(clientToSave);
  }

  // FIXME: 2/21/18 Для отчета нужно создать отдельный контролер. Название мапинга и метода должны быть интуитивно понятны
  @Mapping("/report")
  public void generateReport(@Par("type") String type, @Par("orderBy") String orderBy, @Par("order") int order,
                             @Par("filter") String filter, RequestTunnel requestTunnel) throws Exception {

    if (type == null || !(type.equals("pdf") || type.equals("xlsx"))) {
      throw new Exception("Unsupported File Format");
    }

    String filename = "client_report_" + new Date() + "." + type;
    requestTunnel.setResponseHeader("Content-disposition", "attachment; filename=" + filename);

    this.clientRegister.get().generateReport(requestTunnel.getResponseOutputStream(), type, orderBy, order, filter);

  }


}
