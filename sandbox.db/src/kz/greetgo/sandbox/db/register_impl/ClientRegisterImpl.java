package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.db.Jdbc;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.*;
import kz.greetgo.sandbox.db.model.Sorter;
import kz.greetgo.sandbox.controller.report.*;
import kz.greetgo.sandbox.db.register_impl.jdbc.TestJdbc;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import kz.greetgo.util.RND;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Bean
public class ClientRegisterImpl implements ClientRegister {

    public BeanGetter<ClientDao> clientDao;
    public BeanGetter<AccountDao> accountDao;
    public BeanGetter<CharmDao> charmDao;
    public BeanGetter<AdressDao> adressDao;
    public BeanGetter<PhoneDao> phoneDao;

    private int pageMax = 3;

    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo) {
        clientInfo.id = RND.plusInt(100) + 1;

        Client client = new Client();
        client.id = clientInfo.id;
        client.name = clientInfo.name;
        client.surname = clientInfo.surname;
        client.patronymic = clientInfo.patronymic;
        client.gender = clientInfo.gender;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            client.birth_date = format.parse(clientInfo.birth_date);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("DateFormatError",e);
        }
        client.charm_id = clientInfo.charm_id;

        clientDao.get().insertClient(client);

        addNewPhones(clientInfo);
        addNewAdresses(clientInfo);

        ClientRecord clientRecord = getClientRecord(client);
        return  clientRecord;
    }
    private void addNewPhones(ClientToSave clientToSave) {
        for(String phone : clientToSave.mobilePhones) {
            Phone newPhone = new Phone();
            newPhone.clientID = clientToSave.id;
            newPhone.number = phone;
            newPhone.phoneType = "MOBILE";
            phoneDao.get().insertPhone(newPhone);
        }
        for(String phone : clientToSave.homePhone) {
            Phone newPhone = new Phone();
            newPhone.clientID = clientToSave.id;
            newPhone.number = phone;
            newPhone.phoneType = "HOME";
            phoneDao.get().insertPhone(newPhone);
        }
        for(String phone : clientToSave.workPhone) {
            Phone newPhone = new Phone();
            newPhone.clientID = clientToSave.id;
            newPhone.number = phone;
            newPhone.phoneType = "WORK";
            phoneDao.get().insertPhone(newPhone);
        }
    }
    private void addNewAdresses(ClientToSave clientToSave) {
        if (clientToSave.rAdressStreet != null) {
            Adress adress = new Adress();
            adress.id = Integer.parseInt(String.valueOf(clientToSave.id) + "001");
            adress.clientID = clientToSave.id;
            adress.adressType = "REG";
            adress.street = clientToSave.rAdressStreet;
            adress.house = clientToSave.rAdressHouse;
            adress.flat = clientToSave.rAdressFlat;
            adressDao.get().insertAdress(adress);
        }

        if (clientToSave.fAdressStreet != null) {
            Adress adress = new Adress();
            adress.id = Integer.parseInt(String.valueOf(clientToSave.id) + "002");
            adress.clientID = clientToSave.id;
            adress.adressType = "FACT";
            adress.street = clientToSave.fAdressStreet;
            adress.house = clientToSave.fAdressHouse;
            adress.flat = clientToSave.fAdressFlat;
            adressDao.get().insertAdress(adress);
        }
    }

    @Override
    public ClientRecord updateClient(ClientToSave clientInfo) {
        Client client = new Client();
        client.id = clientInfo.id;
        client.name = clientInfo.name;
        client.surname = clientInfo.surname;
        client.patronymic = clientInfo.patronymic;
        client.gender = clientInfo.gender;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            client.birth_date = format.parse(clientInfo.birth_date);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("DateFormatError",e);
        }
        client.charm_id = clientInfo.charm_id;

        clientDao.get().insertClient(client);

        addNewAdresses(clientInfo);
        addNewPhones(clientInfo);

        return getClientRecord(client);
    }

    @Override
    public String removeClient(String clientID) {
        clientDao.get().removeClient(Integer.parseInt(clientID));

        return clientID;
    }

    @Override
    public ClientDetails getEditableClientInfo(String clientID) {
        Client client = clientDao.get().getClient(Integer.parseInt(clientID));
        ClientDetails clientDetails = client.toClientDetails();

        List<Adress> adresses = adressDao.get().getAdress(Integer.parseInt(clientID));
        for(Adress adress : adresses) {
            clientDetails = adress.toClientDetails(clientDetails);
        }

        List<Phone> phones = phoneDao.get().getPhones(Integer.parseInt(clientID));
        for(Phone phone : phones) {
            clientDetails = phone.toClientDetails(clientDetails);
        }

        return clientDetails;
    }

    @Override
    public ClientToReturn getFilteredClientsInfo(String pageID, String filterStr, String sortBy, String sortOrder) {
        ClientToReturn clientToReturn = new ClientToReturn();

        filterStr += "%" + filterStr + "%";
        List<Client> clients = clientDao.get().getFilteredClients(filterStr);

        clientToReturn.pageCount = getPageNum(clients.size());

        List<ClientRecord> clientRecords = new ArrayList<>();
        for (Client client : clients) {
            clientRecords.add(getClientRecord(client));
        }

        clientRecords = Sorter.sort(clientRecords, sortBy, sortOrder);

        int l = (Integer.parseInt(pageID) - 1) * pageMax;
        int r = l + pageMax;
        int cnt = 0;
        for (ClientRecord client : clientRecords) {
            if (cnt >= l && cnt < r) {
                clientToReturn.clientInfos.add(client);
            }
            cnt++;
        }

        return clientToReturn;
    }
    private ClientRecord getClientRecord(Client client) {
        ClientRecord clientRecord = new ClientRecord();

        clientRecord.id = client.id;
        clientRecord.fio = client.surname + " " + client.name + " " + client.patronymic;
        clientRecord.age = client.CountAge();
        clientRecord.totalCash = getTotalCash(client.id);
        clientRecord.minCash = getMinCash(client.id);
        clientRecord.maxCash = getMaxCash(client.id);
        clientRecord.charm = charmDao.get().getCharm(client.charm_id);

        return clientRecord;
    }
    private float getTotalCash(int clientID){
        Float totalCash;

        totalCash = accountDao.get().getTotalCash(clientID);

        if (totalCash == null) totalCash = (float) 0;

        return totalCash;
    }
    private float getMinCash(int clientID){
        Float minCash;

        minCash = accountDao.get().getTotalCash(clientID);

        if (minCash == null) minCash= (float) 0;

        return minCash;
    }
    private float getMaxCash(int clientID){
        Float maxCash;

        maxCash = accountDao.get().getTotalCash(clientID);

        if (maxCash == null) maxCash= (float) 0;

        return maxCash;
    }
    private int getPageNum(int cnt) {
        int pageNum;
        if (cnt % pageMax == 0) {
            pageNum = cnt / pageMax;
        } else {
            pageNum = cnt / pageMax + 1;
        }

        return pageNum;
    }

    @Override
    public List<Charm> getCharms() {
        List<Charm> charms = charmDao.get().getAllCharms();

        return charms;
    }

    public BeanGetter<JdbcSandbox> jdbcSandbox;

    @Override
    public void genClientListReport(UserInfo userInfo, ClientsListReportView clientsListReportView, String filterStr) {
        jdbcSandbox.get().execute(new TestJdbc(userInfo, clientsListReportView, filterStr));
    }
}
