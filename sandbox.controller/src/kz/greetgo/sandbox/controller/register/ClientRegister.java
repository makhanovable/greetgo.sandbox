package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientToReturn;
import kz.greetgo.sandbox.controller.model.EditableClientInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;

import java.util.List;

public interface ClientRegister {
    
    // TODO: для методов добавления сделать один общий метод. Не должно быть разделение зависимой информации.
    String addNewClient(String clientInfo, String clientID);
    String addNewPhone(String phones, String clientID);
    String addNewAdresses(String adresses, String clientID);
    String removeClient(String clientID);
    EditableClientInfo getEditableClientInfo(String clientID);
    ClientToReturn getFilteredClientsInfo(String pageID, String filterStr);
}
