package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.ClientInfoModel;

public interface ClientRegister {
  ClientInfoModel getClientInfo(int clientId);
}
