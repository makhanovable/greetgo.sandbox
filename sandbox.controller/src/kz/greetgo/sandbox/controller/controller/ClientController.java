package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientInfoModel;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;

@Bean
@Mapping("/client")
public class ClientController {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/info/")
  public ClientInfoModel getClientInfo(@Par("clientId") int clientId) {
    return clientRegister.get().getClientInfo(clientId);
  }

}
