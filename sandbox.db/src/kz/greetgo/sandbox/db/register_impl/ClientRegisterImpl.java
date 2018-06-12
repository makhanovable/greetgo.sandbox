package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;
import static kz.greetgo.sandbox.db.util.ClientHelperUtil.parseDate;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;
    public BeanGetter<JdbcSandbox> jdbc;

    @Override
    public ClientRecordInfo getClientRecords(Options options) {
        return null;
    }

    @Override
    public void deleteClient(int clientId) {
        clientDao.get().deleteFromClient(clientId);
        clientDao.get().deleteFromClientAddr(clientId);
        clientDao.get().deleteFromClientPhone(clientId);
        clientDao.get().deleteFromClientAccount(clientId);
    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) {

        Client client = new Client();
        client.name = details.name;
        client.surname = details.surname;
        client.patronymic = details.patronymic;
        client.gender = Gender.valueOf(details.gender);
        client.birth_date = parseDate(details.birth_date);
        client.charm = details.charm;
        Integer id = clientDao.get().insert_client(client);

        ClientAddr clientAddr = new ClientAddr();
        clientAddr.client = id;
        if (details.addrRegStreet != null && details.addrRegHome != null) {
            clientAddr.type = ClientAddrType.REG;
            clientAddr.street = details.addrRegStreet;
            clientAddr.house = details.addrRegHome;
            clientAddr.flat = details.addrRegFlat;
            clientDao.get().insert_client_addr(clientAddr);
        }
        if (details.addrFactStreet != null && details.addrFactHome != null) {
            clientAddr.type = ClientAddrType.FACT;
            clientAddr.street = details.addrFactStreet;
            clientAddr.house = details.addrFactHome;
            clientAddr.flat = details.addrFactFlat;
            clientDao.get().insert_client_addr(clientAddr);
        }
        // Stream<ClientPhone> stream = Arrays.stream(details.phones); // TODO use stream
        for (int i = 0; i < details.phones.length; i++) {
            if (details.phones[i] != null) {
                ClientPhone clientPhone = new ClientPhone();
                clientPhone.number = details.phones[i].number;
                clientPhone.client = id;
                clientPhone.type = details.phones[i].type; // сверить с клиентом
                clientDao.get().insert_client_phone(clientPhone);
            }
        }

        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id = id;
        clientRecord.name = client.surname + " " + client.name;
        if (client.patronymic != null)
            clientRecord.name += " " + client.patronymic;
        clientRecord.charm = clientDao.get().getCharmById(client.charm);
        clientRecord.age = calculateAge(details.birth_date);
        clientRecord.total = 0f;
        clientRecord.min = 0f;
        clientRecord.max = 0f;
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
        List<Charm> list = new ArrayList<>();
        final String sql = "select * from charm";
        jdbc.get().execute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Charm charm = new Charm();
                        charm.id = rs.getInt("id");
                        charm.name = rs.getString("name");
                        charm.description = rs.getString("description");
                        charm.energy = rs.getFloat("energy");
                        list.add(charm);
                    }
                }
            }
            return list;
        });
        return list;
    }

}
