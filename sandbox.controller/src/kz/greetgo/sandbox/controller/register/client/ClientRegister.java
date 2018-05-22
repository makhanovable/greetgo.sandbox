package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfoModel;
import kz.greetgo.sandbox.controller.model.InfoForm;

public interface ClientRegister {
  ClientInfoModel getClientInfo(int clientId);

  AccountInfo createNewClient(InfoForm createForm);

  AccountInfo editClient(InfoForm editform);

  AccountInfo deleteClient(int clientId);
}
