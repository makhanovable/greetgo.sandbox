package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.EditableClientInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @Mapping("/clientsInfo")
    public List<PrintedClientInfo> clientsInfo() {
        return clientRegister.get().getClientsInfo();
    }

    @ToJson
    @Mapping("/clientsInfoPerPage/{pageID}")
    public List<PrintedClientInfo> clientsInfoPerPage(@ParPath("pageID") String pageID) {
        return clientRegister.get().getClientsInfoPerPage(pageID);
    }

    @AsIs
    @NoSecurity
    @Mapping("/addNewClient")
    public String addNewClient(@Par("clientInfo") String clientInfo, @Par("clientID") String clientID) {
        return clientRegister.get().addNewClient(clientInfo, clientID);
    }

    @AsIs
    @NoSecurity
    @Mapping("/addNewPhone")
    public String addNewPhone(@Par("phones") String phones, @Par("clientID") String clientID) {
        return clientRegister.get().addNewPhone(phones, clientID);
    }

    @AsIs
    @NoSecurity
    @Mapping("/addNewAdress")
    public String addNewAdresses(@Par("adresses") String adresses, @Par("clientID") String clientID) {
        return clientRegister.get().addNewAdresses(adresses, clientID);
    }

    @AsIs
    @NoSecurity
    @Mapping("/removeClient")
    public String removeClient(@Par("clientID") String clientID) {
        return clientRegister.get().removeClient(clientID);
    }

    @ToJson
    @Mapping("/editableClientInfo/{clientID}")
    public EditableClientInfo getEditableClientInfo(@ParPath("clientID") String clientID) {
        return clientRegister.get().getEditableClientInfo(clientID);
    }

    @AsIs
    @Mapping("/clientsInfo/getPagesNum")
    public String getPagesNum() {
        return clientRegister.get().getPagesNum();
    }
}
