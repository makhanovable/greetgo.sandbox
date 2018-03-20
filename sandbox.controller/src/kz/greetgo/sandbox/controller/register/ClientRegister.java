package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.EditableClientInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;

import java.util.List;

public interface ClientRegister {
    String addNewClient(String clientInfo, String clientID);
    String addNewPhone(String phones, String clientID);
    String addNewAdresses(String adresses, String clientID);
    String removeClient(String clientID);
    EditableClientInfo getEditableClientInfo(String clientID);
    List<PrintedClientInfo> getClientsInfo();
    List<PrintedClientInfo> getClientsInfoPerPage(String pageID);
    String getPagesNum();
}
