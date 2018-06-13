package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;
import static kz.greetgo.sandbox.db.util.ClientHelperUtil.parseDate;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;
    public BeanGetter<JdbcSandbox> jdbc;

    @SuppressWarnings({"SqlDialectInspection", "StringConcatenationInLoop"})
    @Override
    public ClientRecordInfo getClientRecords(Options options) {
        ClientRecordInfo clientRecordInfo = new ClientRecordInfo();
        List<ClientRecord> clientRecords = new ArrayList<>();
        clientRecordInfo.items = clientRecords;
        clientRecordInfo.total_count = 0;

        String filter = "";
        if (options.filter != null)
            filter = options.filter;
        String sql = "WITH info (id, name, surname, patronymic, gender, charm, birth_date) AS (" +
                " SELECT id, name, surname, patronymic, gender, charm, birth_date" +
                " FROM client WHERE actual = TRUE AND (name LIKE ? " +
                " OR surname LIKE ? OR patronymic LIKE ?))" +
                " SELECT info.id, info.name, info.surname, info.patronymic, info.gender, info.charm," +
                " info.birth_date, (SELECT min(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) AS min, (SELECT max(client_account.money)" +
                " FROM client_account WHERE client_account.client = info.id) AS max, " +
                " (SELECT sum(client_account.money) FROM client_account" +
                " WHERE client_account.client = info.id) AS total FROM info";

        if (options.sort != null && options.order != null) {
            String temp;
            if (options.sort.equals("name")) {
                temp = " ORDER BY info.surname, info.name, info.patronymic";
                if (options.order.toLowerCase().equals("desc"))
                    temp = " ORDER BY info.surname DESC, info.name DESC, info.patronymic DESC";
                sql += temp;
            } else {
                sql += " ORDER BY ?";
                if (options.order.toLowerCase().equals("desc"))
                    sql += " DESC";
            }
        }
        if (options.page != null && options.size != null) {
            int a = Integer.parseInt(options.page);
            int b = Integer.parseInt(options.size);
            System.out.println(a * b + " a * b"); // TODO hz
            sql += " LIMIT " + options.size;
            if (a * b >= 0)
                sql += " OFFSET " + (a * b);
        }


        String finalSql = sql;
        String finalFilter = filter;
        jdbc.get().execute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(finalSql)) {
                ps.setString(1, "'%" + finalFilter + "'%");
                ps.setString(2, "'%" + finalFilter + "'%");
                ps.setString(3, "'%" + finalFilter + "'%");
                if (options.sort != null && options.order != null)
                    if (!options.sort.equals("name"))
                        ps.setString(4, options.sort);
                ps.setString(5, options.size);
                ps.setString(6, options.page); // TODO
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ClientRecord clientRecord = new ClientRecord();
                        clientRecord.id = rs.getInt("id");
                        clientRecord.name = rs.getString("surname") + " " + rs.getString("name");
                        if (rs.getString("name") != null && !rs.getString("name").isEmpty())
                            clientRecord.name += " " + rs.getString("patronymic");
                        clientRecord.age = calculateAge(rs.getString("birth_date"));
                        clientRecord.charm = clientDao.get().getCharmById(rs.getInt("charm"));
                        clientRecord.total = rs.getFloat("total");
                        clientRecord.min = rs.getFloat("min");
                        clientRecord.max = rs.getFloat("max");
                        if (options.sort != null && options.sort.equals("total") && rs.getFloat("total") == 0.0)
                            continue;
                        else if (options.sort != null && options.sort.equals("max") && rs.getFloat("max") == 0.0)
                            continue;
                        else if (options.sort != null && options.sort.equals("min") && rs.getFloat("min") == 0.0)
                            continue;
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
    public ClientRecord addNewClient(ClientDetails details) { // TODO check valid
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

}


//        SqlSessionFactory sqlSessionFactory = MyBatisSqlSessionFactory.getSqlSessionFactory();
//        sqlSessionFactory.getConfiguration().addMapper(ClientDao.class);
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//
//        try {
//
//        testingMapper.insert(testing);
//        sqlSession.commit();
//        } catch (Exception e) {
//        sqlSession.rollback();
//        } finally{
//        sqlSession.close();
//        }