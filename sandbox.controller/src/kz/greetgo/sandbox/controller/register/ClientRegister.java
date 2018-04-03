package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientRegister {
    
    ClientRecord addNewClient(ClientToSave clientInfo);
    ClientRecord updateClient(ClientToSave clientInfo);
    String removeClient(String clientID);
    ClientDetails getEditableClientInfo(String clientID);
    ClientToReturn getFilteredClientsInfo(String pageID, String filterStr);
    List<Charm> getCharms();
}
