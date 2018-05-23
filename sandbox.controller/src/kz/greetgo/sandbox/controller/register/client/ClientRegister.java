package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;
import kz.greetgo.sandbox.controller.register.account.model.AccountInfoPage;

public interface ClientRegister {
  ClientInfo getClientInfo(int clientId);

  AccountInfo createNewClient(ClientInfo clientInfo);

  AccountInfo editClient(ClientInfo clientInfo);

  AccountInfoPage deleteClient(int clientId, TableRequestDetails requestDetails);
}
