package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.math.BigDecimal;
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
    public ClientRecordInfo getClientRecords(Options options) {
        ClientRecordInfo clientRecordInfo = new ClientRecordInfo();
        List<ClientRecord> clientRecords = new ArrayList<>();
        clientRecordInfo.items = clientRecords;

        options.filter = options.filter != null ? options.filter : "";
        String sql = createSqlForGetClientRecords(options);

        jdbc.get().execute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                // START set params to PreparedStatement
                ps.setString(1, "%" + options.filter + "%");
                ps.setString(2, "%" + options.filter + "%");
                ps.setString(3, "%" + options.filter + "%");

                if (options.page != null && options.size != null) {
                    ps.setBigDecimal(4, new BigDecimal(options.size));
                    ps.setBigDecimal(5, new BigDecimal(options.page)
                            .multiply(new BigDecimal(options.size)));
                }
                // END set params to PreparedStatement

                System.out.println(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ClientRecord clientRecord = new ClientRecord();
                        clientRecord.id = rs.getInt("id");
                        clientRecord.name = rs.getString("name");
                        clientRecord.age = calculateAge(rs.getString("age"));
                        clientRecord.charm = clientDao.get().getCharmById(rs.getInt("charm"));
                        clientRecord.total = rs.getFloat("total");
                        clientRecord.min = rs.getFloat("min");
                        clientRecord.max = rs.getFloat("max");
                        clientRecordInfo.total_count = rs.getInt("count");
                        clientRecords.add(clientRecord);
                    }
                    clientRecordInfo.items = clientRecords;
                }
            }
            return clientRecordInfo;
        });
        return clientRecordInfo;
    }

    @Override
    public void deleteClient(int clientId) {
        clientDao.get().deleteClient(clientId);
        clientDao.get().deleteClientAddr(clientId);
        clientDao.get().deleteClientPhone(clientId);
        clientDao.get().deleteClientAccount(clientId);
    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) {
        if (!isClientDetailsValid(details, true)) // TODO return exception
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
        clientAddr.type = ClientAddrType.REG;
        clientAddr.street = details.addrRegStreet;
        clientAddr.house = details.addrRegHome;
        clientAddr.flat = details.addrRegFlat;
        clientDao.get().insert_client_addr(clientAddr);
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
        if (!isClientDetailsValid(details, false)) // TODO return exception
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
        clientAddr.type = ClientAddrType.REG;
        clientAddr.street = details.addrRegStreet;
        clientAddr.house = details.addrRegHome;
        clientAddr.flat = details.addrRegFlat;
        clientDao.get().edit_client_addr(clientAddr);
        if (details.addrFactStreet != null && details.addrFactHome != null) {
            clientAddr.type = ClientAddrType.FACT;
            clientAddr.street = details.addrFactStreet;
            clientAddr.house = details.addrFactHome;
            clientAddr.flat = details.addrFactFlat;
            clientDao.get().edit_client_addr(clientAddr);
        }
        // Stream<ClientPhone> stream = Arrays.stream(details.phones); // TODO use stream
        for (int i = 0; i < details.phones.length; i++) {
            if (details.phones[i] != null) {
                ClientPhone clientPhone = new ClientPhone();
                clientPhone.number = details.phones[i].number;
                clientPhone.client = details.id;
                clientPhone.type = details.phones[i].type; // сверить с клиентом
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
    public ClientDetails getClientById(int clientId) {
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
            if (clientAddr.type == ClientAddrType.REG) {
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

    private String createSqlForGetClientRecords(Options options) {
        String sql = "WITH info (id, iname, surname, patronymic, gender, charm, birth_date) AS (" +
                " SELECT id, name as iname, surname, patronymic, gender, charm, birth_date" +
                " FROM client WHERE actual = TRUE AND (name LIKE ? " +
                " OR surname LIKE ? OR patronymic LIKE ?))" +
                " SELECT info.id, concat_ws(' ', info.iname, info.surname, info.patronymic) AS name," +
                " info.gender, info.charm," +
                " info.birth_date AS age, CASE WHEN (SELECT min(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) ISNULL THEN 0 ELSE " +
                " (SELECT min(client_account.money) FROM client_account WHERE client_account.client = info.id)" +
                " END AS min, CASE WHEN (SELECT max(client_account.money)" +
                " FROM client_account WHERE client_account.client = info.id) ISNULL THEN 0 ELSE " +
                " (SELECT max(client_account.money) FROM client_account WHERE client_account.client = info.id)" +
                " END AS max, CASE WHEN (SELECT sum(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) ISNULL THEN 0 ELSE (SELECT sum(client_account.money)" +
                " FROM client_account WHERE client_account.client = info.id) END AS total," +
                " (SELECT count(info) FROM info) AS count FROM info";

        if (isValidSortOptions(options.sort, options.order)) {
            sql += " ORDER BY " + options.sort;
            if (options.sort.equalsIgnoreCase("age")) {
                if (options.order.equalsIgnoreCase("asc"))
                    sql+= " DESC";
            } else if (options.order.equalsIgnoreCase("asc"))
                sql+= " NULLS FIRST";
            else
                sql+= " DESC NULLS LAST";
        }

        if (options.page != null && options.size != null)
            sql += " LIMIT ? OFFSET ?";
        return sql;
    }

    private boolean isValidSortOptions(String sort, String order) {
        return sort != null
                && order != null
                && (sort.equalsIgnoreCase("name") ||
                    sort.equalsIgnoreCase("age") ||
                    sort.equalsIgnoreCase("total") ||
                    sort.equalsIgnoreCase("max") ||
                    sort.equalsIgnoreCase("min"))
                && (order.equalsIgnoreCase("desc") ||
                    order.equalsIgnoreCase("asc"));
    }

}