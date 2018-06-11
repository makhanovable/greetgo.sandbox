package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;

    @Override
    public ClientRecordInfo getClientRecords(Options options) {
        return null;
    }

    @Override
    public void deleteClient(int clientId) {
    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) { // TODO лапша
        Client client = new Client();
        ClientAddr clientAddr = new ClientAddr();
        ClientPhone clientPhone = new ClientPhone();
        ClientAccount clientAccount = new ClientAccount();

        client.id = 1001;
        client.name = details.name;
        client.surname = details.surname;
        client.patronymic = details.patronymic;
        client.gender = Gender.FEMALE;
        client.birth_date = new Date();
        client.charm = details.charm;
        clientDao.get().insert_client(client);

        ClientRecord clientRecord = new ClientRecord();
        clientRecord.name = client.surname + " " + client.name + " " + client.patronymic;
        clientRecord.age = 0;
        clientRecord.total = 0;
        clientRecord.max = 0;
        clientRecord.min = 0;
        clientRecord.charm = clientDao.get().getCharmById(details.charm);
        return clientRecord;
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
