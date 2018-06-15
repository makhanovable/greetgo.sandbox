package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDetailsDot;
import kz.greetgo.sandbox.db.stand.model.ClientRecordDot;

import java.util.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

    public BeanGetter<ClientStandDb> db;

    @Override
    public ClientRecordInfo getClientRecords(Options options) {
        ClientRecordInfo wrapper = new ClientRecordInfo();
        List<ClientRecord> out = new ArrayList<>();
        for (ClientRecordDot dot : db.get().getClientRecordStorage(options)) {
            ClientRecord clientRecord = new ClientRecord();
            clientRecord.id = dot.id;
            clientRecord.name = dot.name;
            clientRecord.charm = dot.charm;
            clientRecord.age = dot.age;
            clientRecord.total = dot.total;
            clientRecord.max = dot.max;
            clientRecord.min = dot.min;
            out.add(clientRecord);
        }
        wrapper.total_count = db.get().out.size();
        wrapper.items = out;
        return wrapper;
    }

    @Override
    public void deleteClient(int clientId) {
        db.get().deleteClientInfo(clientId);
    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) {
        ClientRecord clientRecord = new ClientRecord();
        ClientRecordDot dot = db.get().addNewClientRecord(details);
        clientRecord.id = dot.id;
        clientRecord.name = dot.name;
        clientRecord.charm = dot.charm;
        clientRecord.age = dot.age;
        clientRecord.total = dot.total;
        clientRecord.max = dot.max;
        clientRecord.min = dot.min;
        return clientRecord;
    }

    @Override
    public ClientRecord editClient(ClientDetails details) {
        ClientRecord clientRecord = new ClientRecord();
        ClientRecordDot dot = db.get().editClientRecord(details);
        clientRecord.id = dot.id;
        clientRecord.name = dot.name;
        clientRecord.charm = dot.charm;
        clientRecord.age = dot.age;
        clientRecord.total = dot.total;
        clientRecord.max = dot.max;
        clientRecord.min = dot.min;
        return clientRecord;
    }

    @Override
    public ClientDetails getClientById(int clientId) {
        ClientDetails clientDetails = new ClientDetails();
        ClientDetailsDot dot = db.get().getClientDetailById(clientId);
        clientDetails.id = dot.id;
        clientDetails.name = dot.name;
        clientDetails.surname = dot.surname;
        clientDetails.patronymic = dot.patronymic;
        clientDetails.gender = dot.gender;
        clientDetails.birth_date = dot.birth_date;
        clientDetails.charm = dot.charm;
        clientDetails.addrFactStreet = dot.addrFactStreet;
        clientDetails.addrFactHome = dot.addrFactHome;
        clientDetails.addrFactFlat = dot.addrFactFlat;
        clientDetails.addrRegStreet = dot.addrRegStreet;
        clientDetails.addrRegHome = dot.addrRegHome;
        clientDetails.addrRegFlat = dot.addrRegFlat;
        ClientPhone[] phones = new ClientPhone[5];
        for (int i = 0; i < dot.phones.length; i++) {
            phones[i] = new ClientPhone();
            if (dot.phones[i].number == null)
                phones[i].number = "";
            else
                phones[i].number = dot.phones[i].number;
            phones[i].type = dot.phones[i].type;
        }
        clientDetails.phones = phones;
        return clientDetails;
    }

    @Override
    public List<Charm> getCharms() {
        List<Charm> out = new ArrayList<>();
        for (CharmDot dot : db.get().charmsStorage) {
            Charm charm = new Charm();
            charm.id = dot.id;
            charm.name = dot.name;
            out.add(charm);
        }
        return out;
    }

    @Override
    public void renderClientList(Options options) {
        // TODO impl
    }

}
