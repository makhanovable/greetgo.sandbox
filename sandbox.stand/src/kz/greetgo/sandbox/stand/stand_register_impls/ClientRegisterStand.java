package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToReturn;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.Account;
import kz.greetgo.sandbox.db.stand.model.Charm;
import kz.greetgo.sandbox.db.stand.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Bean
public class ClientRegisterStand  implements ClientRegister{

    public BeanGetter<StandDb> db;
    private int pageMax = 6;

    //TODO: Также, при сохранении записи, надо возвращать эту же запись клиенту.
    // Подумай почему, тоже спрошу.
    //TODO: объект *ToSave учавствует в сохранение, а нужно вернуть объект *Record
    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo, String clientID) {
        clientID = db.get().addNewCLient(clientInfo, clientID);

        return getClientRecord(clientID);
    }

    @Override
    public ClientRecord updateClient(ClientToSave clientInfo) {
        String clientID = db.get().updateClient(clientInfo);

        return getClientRecord(clientID);
    }

    @Override
    public String removeClient(String clientID) {
        db.get().removeClient(clientID);
        return clientID;
    }

    @Override
    public ClientDetails getEditableClientInfo(String clientID) {
        ClientDetails clientInfo = db.get().getEditableClientInfo(clientID);
        return clientInfo;
    }

    @Override
    public ClientToReturn getFilteredClientsInfo(String pageID, String filterStr) {
        ClientToReturn clientToReturn = new ClientToReturn();

        List<Client> clients = new ArrayList<Client>();

        if (filterStr != null) {
            for (Client client : db.get().clientStorage.values()) {
                if (client.name.toLowerCase().contains(filterStr.toLowerCase()) ||
                    client.surname.toLowerCase().contains(filterStr.toLowerCase()) ||
                    client.patronymic.toLowerCase().contains(filterStr.toLowerCase())) {
                        clients.add(client);
                }
            }
        } else {
            clients.addAll(db.get().clientStorage.values());
        }

        clientToReturn.pageCount = getPageNum(clients.size());

        List<ClientRecord> clientInfos = new ArrayList<ClientRecord>();

        int l = (Integer.parseInt(pageID) - 1) * pageMax;
        int r = l + pageMax;
        int cnt = 0;
        for (Client client : clients) {
            if (cnt >= l && cnt < r) {
                ClientRecord clientInfo = new ClientRecord();
                clientInfo.id = client.id;
                clientInfo.fio = client.name + " " + client.patronymic + " " + client.surname;
                clientInfo.age = client.CountAge();
                clientInfo.totalCash = getTotalCash(client.id);
                clientInfo.maxCash = getMaxCash(client.id);
                clientInfo.minCash = getMinCash(client.id);
                clientInfo.charm = getCharm(client.charmID);

                clientInfos.add(clientInfo);
            }
            cnt++;
        }

        clientToReturn.clientInfos = clientInfos;

        return clientToReturn;
    }

    @Override
    public List<String> getCharms() {
        List<String> charms = new ArrayList<String>();

        for (Charm charm : db.get().charmStorage.values()) {
            charms.add(charm.name);
        }

        return charms;
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

    private float getTotalCash(String clientId) {
        float totalCash = 0;

        for (Account account : db.get().accountStorage.values()) {
            if (Objects.equals(account.clientID,clientId)) {
                totalCash += account.money;
            }
        }

        return totalCash;
    }
    private float getMinCash(String clientId) {
        float minCash = -1;

        for (Account acc : db.get().accountStorage.values()) {
            if (Objects.equals(acc.clientID,clientId)) {
                if (minCash == -1) minCash = acc.money; else
                if (acc.money < minCash) minCash = acc.money;
            }
        }

        return minCash;
    }
    private float getMaxCash(String clientId) {
        float maxCash = 0;

        for (Account acc : db.get().accountStorage.values()) {
            if (Objects.equals(acc.clientID,clientId)) {
                if (acc.money > maxCash) maxCash = acc.money;
            }
        }

        return maxCash;
    }

    private String getCharm(String charmID) {
        for (Charm charm : db.get().charmStorage.values()) {
            if (Objects.equals(charm.id, charmID)) {
                return charm.name;
            }
        }

        return null;
    }

    public ClientRecord getClientRecord(String clientID) {
        Client client = db.get().clientStorage.get(clientID);

        ClientRecord clientInfo = new ClientRecord();
        clientInfo.id = client.id;
        clientInfo.fio = client.name + " " + client.patronymic + " " + client.surname;
        clientInfo.age = client.CountAge();
        clientInfo.totalCash = getTotalCash(client.id);
        clientInfo.maxCash = getMaxCash(client.id);
        clientInfo.minCash = getMinCash(client.id);
        clientInfo.charm = getCharm(client.charmID);

        return clientInfo;
    }
}
