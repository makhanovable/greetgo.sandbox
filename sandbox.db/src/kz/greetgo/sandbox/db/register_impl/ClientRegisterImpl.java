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
    public BeanGetter<ReportParamsDao> reportParamsDao;
    public BeanGetter<ConfigParamsDao> configParamsDao;

    //TODO: константой конфигурационные параметры нельзя хранить.
//    private int pageMax = 3;

    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo) {
        
        //TODO: Верная генерация, если клиентов максимум будет такое количество.
        //Наши же системы разрабатываются для огромных компаний. 
        //Представь, что было бы с нашей системой, которая сейчас стоит в Китае, если бы мы так id генерировали.
        //Переделать.
        clientInfo.id = RND.plusInt(Integer.MAX_VALUE) + 1;

        Client client = new Client();
        client.id = clientInfo.id;
        if (clientInfo.name == null || clientInfo.surname == null ||
                clientInfo.mobilePhones == null || clientInfo.rAdressStreet == null ||
                clientInfo.rAdressHouse == null || clientInfo.rAdressFlat == null) {
            throw new RuntimeException("Null values");
        } else {
            client.name = clientInfo.name;
            client.surname = clientInfo.surname;
            client.patronymic = clientInfo.patronymic;
            client.gender = clientInfo.gender;
            client.charm_id = clientInfo.charm_id;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            client.birth_date = format.parse(clientInfo.birth_date);
        } catch (Exception e) {
            //TODO: Молодец. Хоршое решение. Объяснишь, почему так сделал.
            //TODO: Также, этот участок можно заменить на более компактный. Замени здесь и в других подобных местах.
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("DateFormatError",e);
        }

        clientDao.get().insertClient(client);

        addNewPhones(clientInfo);
        addNewAdresses(clientInfo);

        //TODO: Упрости нижние две строчки.
        return getClientRecord(client);
    }
    private void addNewPhones(ClientToSave clientToSave) {
        for(String phone : clientToSave.mobilePhones) {
            Phone newPhone = new Phone();
            newPhone.clientID = clientToSave.id;
            newPhone.number = phone;
            //TODO: неверное присвоение типов. 
            //Если где-то ещё будет такое же присвоение и программисту нужно будет менять значение типа
            //то нужно будет менять в двух местах, а может и больше.
            //Поменять здесь и в других местах, где идёт присвоение типов.
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
            //TODO: неверное присвоение типов. 
            //Если где-то ещё будет такое же присвоение и программисту нужно будет менять значение типа
            //то нужно будет менять в двух местах, а может и больше.
            //Поменять здесь и в других местах, где идёт присвоение типов.
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
    public ClientToReturn getFilteredClientsInfo(ClientsListParams clientsListParams) {
        ClientToReturn clientToReturn = new ClientToReturn();

        //TODO: структура таблицы неверна
        int pageMax = configParamsDao.get().getPageMax();

        int pageID = clientsListParams.pageID;
        String filterStr = clientsListParams.filterSortParams.filterStr;
        String sortBy = clientsListParams.filterSortParams.sortBy;
        String sortOrder = clientsListParams.filterSortParams.sortOrder;

        filterStr = "%" + filterStr + "%";
        List<Client> clients = clientDao.get().getFilteredClients(filterStr);

        clientToReturn.pageCount = getPageNum(clients.size(), pageMax);

        List<ClientRecord> clientRecords = new ArrayList<>();
        for (Client client : clients) {
            clientRecords.add(getClientRecord(client));
        }

        clientRecords = Sorter.sort(clientRecords, sortBy, sortOrder);

        int l = (pageID - 1) * pageMax;
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
    private int getPageNum(int cnt, int pageMax) {
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
    public void genClientListReport(ClientsListReportParams clientsListReportParams) {
        String username = clientsListReportParams.username;
        ClientsListReportView clientsListReportView= clientsListReportParams.view;
        String filterStr = clientsListReportParams.filterSortParams.filterStr;
        String sortBy = clientsListReportParams.filterSortParams.sortBy;
        String sortOrder = clientsListReportParams.filterSortParams.sortOrder;
        jdbcSandbox.get().execute(new TestJdbc(username, clientsListReportView, filterStr, sortBy, sortOrder));
    }

    @Override
    public int saveReportParams(ReportParamsToSave reportParamsToSave) {
        reportParamsDao.get().insertReportParams(reportParamsToSave);
        return reportParamsToSave.report_id;
    }

    @Override
    public ReportParamsToSave popReportParams(int report_id) {
        ReportParamsToSave reportParamsToSave = reportParamsDao.get().getReportParams(report_id);
        reportParamsDao.get().removeRepostParams(report_id);

        return reportParamsToSave;
    }
}
