package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/addOrUpdate")
  public void addOrUpdate(@Par("client") @Json ClientToSave clientToSave) {
    this.clientRegister.get().addOrUpdate(clientToSave);
  }

  @ToJson
  @Mapping("/getDetail")
  public ClientDetail getDetail(@Par("id") String id) {
    return this.clientRegister.get().getDetail(id);
  }

  @ToJson
  @Mapping("/removeClients")
  public int removeClients(@Par("ids") @Json List<String> ids) {
    return this.clientRegister.get().removeClients(ids);
  }

  @ToJson
  @Mapping("/getList")
  public List<ClientRecord> getList(@Par("limit") int limit, @Par("page") int page, @Par("filter") String filter,
                                    @Par("orderBy") String orderBy, @Par("desc") int desc) {

    return clientRegister.get().getClientRecordList(limit, page, filter, orderBy, desc);
  }

  @ToJson
  @Mapping("/getNumberOfClients")
  public long getNumberOfClients(@Par("filter") String filter) {
    return this.clientRegister.get().getNumberOfClients(filter);
  }

}
