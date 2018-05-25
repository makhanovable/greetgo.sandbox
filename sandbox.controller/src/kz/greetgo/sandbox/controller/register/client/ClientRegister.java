package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.*;

public interface ClientRegister {
  ClientDetails getClientDetails(int clientId);

  ClientAccountInfo createNewClient(ClientToSave clientToSave);

  ClientAccountInfo editClient(ClientToSave clientToSave);

  ClientAccountInfoPage deleteClient(int clientId, TableRequestDetails requestDetails);
}
