package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToReturn;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.List;

public interface ClientRegister {
    
    // TODO: для методов добавления сделать один общий метод. Не должно быть разделение зависимой информации.
    ClientRecord addNewClient(ClientToSave clientInfo, String clientID);
    ClientRecord updateClient(ClientToSave clientInfo);
    String removeClient(String clientID);
    ClientDetails getEditableClientInfo(String clientID);
    ClientToReturn getFilteredClientsInfo(String pageID, String filterStr);
    List<String> getCharms();
}
