package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {
    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ClientRecord updateClient(ClientToSave clientInfo) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String removeClient(String clientID) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ClientDetails getEditableClientInfo(String clientID) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ClientToReturn getFilteredClientsInfo(String pageID, String filterStr, String sortBy, String sortOrder) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<Charm> getCharms() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
