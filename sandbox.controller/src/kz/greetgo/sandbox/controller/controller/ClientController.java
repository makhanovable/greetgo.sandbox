package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

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
  public List<ClientInfo> list(@Par("limit") int limit, @Par("page") int page, @Par("filter") String filter,
                               @Par("orderBy") String orderBy, @Par("desc") int desc) {

    return clientRegister.get().getClientInfoList(limit, page, filter, orderBy, desc);
  }

  @ToJson
  @Mapping("/amount")
  public int getAmount(@Par("filter") String filter) {
    return this.clientRegister.get().getClientsSize(filter);
  }

  @ToJson
  @Mapping("/remove")
  public float removeClients(@Par("ids") @Json List<Integer> ids) {
    return this.clientRegister.get().remove(ids);
  }

  @ToJson
  @Mapping("/info")
  public ClientForm info(@Par("id") int id) {
    return this.clientRegister.get().info(id);
  }

  @ToJson
  @Mapping("/add")
  public String add(@Par("client") @Json ClientForm clientForm) {
    this.clientRegister.get().add(clientForm);
    return "ok";
  }

  @ToJson
  @Mapping("/update")
  public String update(@Par("client") @Json ClientForm clientForm) {
    return this.clientRegister.get().update(clientForm) ? "ok" : "bad";
  }

}