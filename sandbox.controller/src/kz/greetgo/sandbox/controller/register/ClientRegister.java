package kz.greetgo.sandbox.controller.register;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.report.*;

import java.util.List;

public interface ClientRegister {
    
    ClientRecord addNewClient(ClientToSave clientInfo);

    ClientRecord updateClient(ClientToSave clientInfo);

    String removeClient(String clientID);

    ClientDetails getEditableClientInfo(String clientID);

    ClientToReturn getFilteredClientsInfo(String pageID, String filterStr, String sortBy, String sortOrder);

    List<Charm> getCharms();

    void genClientListReport(UserInfo userInfo, ClientsListReportView clientsListReportView, String filterStr);
}
