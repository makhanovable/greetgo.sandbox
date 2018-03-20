package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientToReturn;
import kz.greetgo.sandbox.controller.model.EditableClientInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;
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

    //TODO: Статусы так не возвращаются. Для этого есть специальные методы.
    //TODO: Также, при сохранении записи, надо возвращать эту же запись клиенту.
    // Подумай почему, тоже спрошу.
    @Override
    public String addNewClient(String clientInfo, String clientID) {
        db.get().addNewCLient(clientInfo , clientID);
        return clientInfo;
    }

    @Override
    public String addNewPhone(String phones, String clientID) {
        db.get().addNewPhones(phones, clientID);
        return phones;
    }

    @Override
    public String addNewAdresses(String adresses, String clientID) {
        db.get().addNewAdresses(adresses, clientID);
        return adresses;
    }

    @Override
    public String removeClient(String clientID) {
        db.get().removeClient(clientID);
        return clientID;
    }

    @Override
    public EditableClientInfo getEditableClientInfo(String clientID) {
        EditableClientInfo clientInfo = db.get().getEditableClientInfo(clientID);
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

        List<PrintedClientInfo> clientInfos = new ArrayList<PrintedClientInfo>();

        int l = (Integer.parseInt(pageID) - 1) * pageMax;
        int r = l + pageMax;
        int cnt = 0;
        for (Client client : clients) {
            if (cnt >= l && cnt < r) {
                PrintedClientInfo clientInfo = new PrintedClientInfo();
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
}
