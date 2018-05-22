package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfo;

public interface ClientRegister {
  ClientInfo getClientInfo(int clientId);

  AccountInfo createNewClient(ClientInfo clientInfo);

  AccountInfo editClient(ClientInfo clientInfo);

  AccountInfo deleteClient(int clientId);
}
