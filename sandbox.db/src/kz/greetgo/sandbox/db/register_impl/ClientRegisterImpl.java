package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordWrapper;
import kz.greetgo.sandbox.controller.model.Options;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    @Override
    public ClientRecordWrapper getClientRecords(Options options) {
        return null;
    }

    @Override
    public void deleteClient(int clientId) {

    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) {
        return null;
    }

    @Override
    public ClientRecord editClient(ClientDetails details) {
        return null;
    }

    @Override
    public ClientDetails getClientById(int clientId) {
        return null;
    }

    @Override
    public List<Charm> getCharms() {
        return null;
    }
}
