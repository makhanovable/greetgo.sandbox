package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;
import kz.greetgo.sandbox.controller.register.account.model.AccountInfoPage;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/info")
  public ClientInfo getClientInfo(@Par("clientId") int clientId) {
    return clientRegister.get().getClientInfo(clientId);
  }

  @ToJson()
  @Mapping("/create")
  public AccountInfo createNewClient(@Json @Par("clientInfo") ClientInfo clientInfo) {
    return clientRegister.get().createNewClient(clientInfo);
  }

  @ToJson()
  @Mapping("/edit")
  public AccountInfo editClient(@Json @Par("clientInfo") ClientInfo clientInfo) {
    return clientRegister.get().editClient(clientInfo);
  }

  @ToJson()
  @Mapping("/delete")
  public AccountInfoPage deleteClient(@Par("clientId") int clientId,
                                      @Json @Par("requestDetails") TableRequestDetails requestDetails) {
    return clientRegister.get().deleteClient(clientId, requestDetails);
  }
}
