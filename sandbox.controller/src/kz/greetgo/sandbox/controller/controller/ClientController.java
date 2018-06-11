package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordInfo;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @NoSecurity
    @Mapping("/get_clients_list")
    public ClientRecordInfo getClientRecords(@ParamsTo Options options) {
        return clientRegister.get().getClientRecords(options);
    }

    @ToJson
    @NoSecurity
    @Mapping("/add_new_client")
    public ClientRecord addNewClientRecord(@Json @Par("clientToSave") ClientDetails clientDetails) {
        return clientRegister.get().addNewClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/del_client")
    public void deleteClient(@Par("clientId") int clientId) {
        clientRegister.get().deleteClient(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/edit_client")
    public ClientRecord editClient(@Json @Par("clientToSave") ClientDetails clientDetails) {
        return clientRegister.get().editClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_client_info_by_id")
    public ClientDetails getClientById(@Par("clientId") int clientId) {
        return clientRegister.get().getClientById(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_charms")
    public List<Charm> getCharms() {
        return clientRegister.get().getCharms();
    }

}
