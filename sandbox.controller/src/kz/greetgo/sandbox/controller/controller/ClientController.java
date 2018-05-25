package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/info")
  public ClientDetails getClientInfo(@Par("clientId") int clientId) {
    return clientRegister.get().getClientDetails(clientId);
  }

  @ToJson()
  @Mapping("/create")
  public ClientAccountInfo createNewClient(@Json @Par("clientToSave") ClientToSave clientToSave) {
    return clientRegister.get().createNewClient(clientToSave);
  }

  @ToJson()
  @Mapping("/edit")
  public ClientAccountInfo editClient(@Json @Par("clientToSave") ClientToSave clientToSave) {
    return clientRegister.get().editClient(clientToSave);
  }

  @ToJson()
  @Mapping("/delete")
  public ClientAccountInfoPage deleteClient(@Par("clientId") int clientId,
                                            @Json @Par("requestDetails") TableRequestDetails requestDetails) {
    return clientRegister.get().deleteClient(clientId, requestDetails);
  }
}
