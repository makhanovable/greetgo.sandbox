package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.callback.ClientRecordsCallback;
import kz.greetgo.sandbox.db.register_impl.callback.ClientRecordsReportCallback;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;
import static kz.greetgo.sandbox.db.util.ClientHelperUtil.isClientDetailsValid;
import static kz.greetgo.sandbox.db.util.FlexibleDateParser.parseDate;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;
    public BeanGetter<JdbcSandbox> jdbc;

    @Override
    public List<ClientRecord> getClientList(RequestOptions options) {
        return jdbc.get().execute(new ClientRecordsCallback(options, clientDao));
    }

    @Override
    public int getClientListCount(String filter) {
        return clientDao.get().getClientRecordsCount(filter);
    }

    @Override
    public void deleteClient(int clientId) {
        clientDao.get().deleteClient(clientId);
    }

    @Override
    public ClientRecord addClient(ClientDetails details) {
        if (!isClientDetailsValid(details, true))
            return null;
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
        clientAddr.type = AddrType.REG;
        clientAddr.street = details.addrRegStreet;
        clientAddr.house = details.addrRegHome;
        clientAddr.flat = details.addrRegFlat;
        clientDao.get().insert_client_addr(clientAddr);
        if (details.addrFactStreet != null && details.addrFactHome != null) {
            clientAddr.type = AddrType.FACT;
            clientAddr.street = details.addrFactStreet;
            clientAddr.house = details.addrFactHome;
            clientAddr.flat = details.addrFactFlat;
            clientDao.get().insert_client_addr(clientAddr);
        }
        // Stream<ClientPhone> stream = Arrays.stream(details.phones); //
        for (int i = 0; i < details.phones.length; i++) {
            if (details.phones[i] != null) {
                ClientPhone clientPhone = new ClientPhone();
                clientPhone.number = details.phones[i].number;
                clientPhone.client = id;
                clientPhone.type = details.phones[i].type; //
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
        if (!isClientDetailsValid(details, false))
            return null;
        Client client = new Client();
        client.id = details.id;
        client.name = details.name;
        client.surname = details.surname;
        client.patronymic = details.patronymic;
        client.gender = Gender.valueOf(details.gender);
        client.birth_date = parseDate(details.birth_date);
        client.charm = details.charm;
        clientDao.get().edit_client(client);

        ClientAddr clientAddr = new ClientAddr();
        clientAddr.client = details.id;
        clientAddr.type = AddrType.REG;
        clientAddr.street = details.addrRegStreet;
        clientAddr.house = details.addrRegHome;
        clientAddr.flat = details.addrRegFlat;
        clientDao.get().edit_client_addr(clientAddr);
        if (details.addrFactStreet != null && details.addrFactHome != null) {
            clientAddr.type = AddrType.FACT;
            clientAddr.street = details.addrFactStreet;
            clientAddr.house = details.addrFactHome;
            clientAddr.flat = details.addrFactFlat;
            clientDao.get().edit_client_addr(clientAddr);
        }
        // Stream<ClientPhone> stream = Arrays.stream(details.phones);
        for (int i = 0; i < details.phones.length; i++) {
            if (details.phones[i] != null) {
                ClientPhone clientPhone = new ClientPhone();
                clientPhone.number = details.phones[i].number;
                clientPhone.client = details.id;
                clientPhone.type = details.phones[i].type;
                clientDao.get().edit_client_phone(clientPhone);
            }
        }

        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id = details.id;
        clientRecord.name = client.surname + " " + client.name;
        if (client.patronymic != null)
            clientRecord.name += " " + client.patronymic;
        clientRecord.charm = clientDao.get().getCharmById(client.charm);
        clientRecord.age = calculateAge(details.birth_date);
        List<Float> list = clientDao.get().getClientAccountsMoneyById(details.id);
        Collections.sort(list);
        clientRecord.total = 0f;
        for (float i : list)
            clientRecord.total += i;
        clientRecord.min = list.isEmpty() ? 0f : list.get(0);
        clientRecord.max = list.isEmpty() ? 0f : list.get(list.size() - 1);
        return clientRecord;
    }

    @Override
    public ClientDetails getClientDetails(int clientId) {
        ClientDetails clientDetails = new ClientDetails();
        Client client = clientDao.get().getClientByID(clientId);
        List<ClientAddr> clientAddrs = clientDao.get().getClientAddrsByID(clientId);
        List<ClientPhone> clientPhones = clientDao.get().getClientPhonesByID(clientId);

        clientDetails.id = clientId;
        clientDetails.name = client.name;
        clientDetails.surname = client.surname;
        clientDetails.patronymic = client.patronymic;
        clientDetails.gender = client.gender.name();
        clientDetails.birth_date = client.birth_date.toString();
        clientDetails.charm = client.charm;
        clientDetails.phones = clientPhones.toArray(new ClientPhone[clientPhones.size()]);

        for (ClientAddr clientAddr : clientAddrs) {
            if (clientAddr.type == AddrType.REG) {
                clientDetails.addrRegStreet = clientAddr.street;
                clientDetails.addrRegHome = clientAddr.house;
                clientDetails.addrRegFlat = clientAddr.flat;
            } else {
                clientDetails.addrFactStreet = clientAddr.street;
                clientDetails.addrFactHome = clientAddr.house;
                clientDetails.addrFactFlat = clientAddr.flat;
            }
        }
        return clientDetails;
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

    @Override
    public void renderClientList(RequestOptions options,
                                 ClientRecordsReportView view,
                                 String username, String link) {
        view.start();
        jdbc.get().execute(new ClientRecordsReportCallback(options, clientDao, view));
        view.finish(username, new Date(), link);
    }

}