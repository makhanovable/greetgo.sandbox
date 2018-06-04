package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ResponseClientListWrapper;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {


    @Override
    public ResponseClientListWrapper getClientsList(String filter, String sort, String order, String page, String size) {
        return null;
    }

    @Override
    public void addNewClient(Client client, List<ClientAddr> addrs, List<ClientPhone> phones) {

    }

    @Override
    public void delClient(String clientId) {

    }

    @Override
    public void editClient(Client client, List<ClientAddr> addrs, List<ClientPhone> phones) {

    }

    @Override
    public ClientInfoResponseTest getClientById(String clientId) {
        return null;
    }

    @Override
    public List<Charm> getCharms() {
        return null;
    }

}
