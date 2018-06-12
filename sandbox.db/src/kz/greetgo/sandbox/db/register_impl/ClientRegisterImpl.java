package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;
    public BeanGetter<JdbcSandbox> jdbc;

    @Override
    public ClientRecordInfo getClientRecords(Options options) {
        final String testSql = "select * from client";
        jdbc.get().execute(connection -> {

            try(PreparedStatement ps = connection.prepareStatement(testSql)){
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()){
                        ClientRecord clientRecord = new ClientRecord();
                        clientRecord.name = rs.getString("name");
                    }
                }

            }
            return null;
        });
        return null;
    }

    @Override
    public void deleteClient(int clientId) {
    }

    @Override
    public ClientRecord addNewClient(ClientDetails details) {
        int id = generateID();
//
//        Client client = new Client();
//        client.id = id;
//        client.name = details.name;
//        client.surname = details.surname;
//        client.patronymic = details.patronymic;
//        client.gender = Gender.FEMALE;
//        client.birth_date = new Date();
//        client.charm = details.charm;
//        clientDao.get().insert_client(client);
//
//        ClientAddr clientAddr = new ClientAddr();
//        clientAddr.client = id;
//        if (details.addrRegStreet != null || details.addrRegHome != null || details.addrRegFlat != null){
//            clientAddr.type = ClientAddrType.REG;
//            clientAddr.street = details.addrRegStreet;
//            clientAddr.house = details.addrRegHome;
//            clientAddr.flat = details.addrRegFlat;
//            clientDao.get().insert_client_addr(clientAddr);
//        }
//        if (details.addrFactStreet != null || details.addrFactHome != null || details.addrFactFlat != null){
//            clientAddr.type = ClientAddrType.FACT;
//            clientAddr.street = details.addrFactStreet;
//            clientAddr.house = details.addrFactHome;
//            clientAddr.flat = details.addrFactFlat;
//            clientDao.get().insert_client_addr(clientAddr);
//        }
//        for (int i = 0; i < 5; i++) {
//            if(details.phones[i] != null) {
//                ClientPhone clientPhone = new ClientPhone();
////                clientPhone.number = details.phones[i];
//                clientPhone.client = id;
//                clientPhone.type = PhoneType.WORK;
//                clientDao.get().insert_client_phone(clientPhone);
//            }
//        }
//
//        ClientAccount clientAccount = new ClientAccount();
//        clientAccount.id  = id;
//        clientAccount.client = id;
//        clientAccount.money = 0;
//        clientAccount.number = RND.str(10);
//        clientAccount.registered_at = null;
//        clientDao.get().insert_client_account(clientAccount);

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

    private int generateID() {
        Integer id = clientDao.get().getLastID();
        if (id == null) {
            clientDao.get().insertID(0);
            id = 0;
        }
        id++;
        clientDao.get().setLastID(id);
        return id;
    }

    private ClientRecord getClientRecordById(int id) {
//        Client client = clientDao.get().getClientById(id);
//        ClientAccount clientAccount = clientDao.get().getClientAccountById(id);
        ClientRecord clientRecord = new ClientRecord();
//
//        clientRecord.id = id;
//        clientRecord.name = client.surname + " " + client.name + " " + client.patronymic;
//        clientRecord.charm = clientDao.get().getCharmById(client.charm);
//        clientRecord.age = calculateAge(client.birth_date);
//        clientRecord.total = clientAccount.money;
//        clientRecord.min = clientAccount.money;
//        clientRecord.max = clientAccount.money;
        return clientRecord;
    }

    private int calculateAge(Date birth) {
        return 0;
    }

}
