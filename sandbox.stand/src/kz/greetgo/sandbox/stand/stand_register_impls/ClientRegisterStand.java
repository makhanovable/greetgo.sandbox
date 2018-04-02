package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Bean
public class ClientRegisterStand  implements ClientRegister{

    public BeanGetter<StandDb> db;
    private int pageMax = 6;

    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo) {
        String clientID = db.get().addNewCLient(clientInfo);

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
        ClientDetails clientDetails = db.get().clientStorage.get(clientID).toClientDetails();
        clientDetails.charm = db.get().charmStorage.get(db.get().clientStorage.get(clientID).charmID).name;

        for (AdressDot adressDot : db.get().adressStorage.values()) {
            if (Objects.equals(adressDot.clientID, clientID)) {
                adressDot.toClientDetails(clientDetails);
            }
        }

        for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
            if (phoneDot.clientID.equals(clientID)) {
                phoneDot.toClientDetails(clientDetails);
            }
        }

        return clientDetails;
    }

    @Override
    public ClientToReturn getFilteredClientsInfo(String pageID, String filterStr) {
        ClientToReturn clientToReturn = new ClientToReturn();

        List<ClientDot> clients = new ArrayList<ClientDot>();

        if (filterStr != null) {
            for (ClientDot client : db.get().clientStorage.values()) {
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
        for (ClientDot client : clients) {
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

    //TODO: Нужно возвращать не только имена но и их идентификаторы
    @Override
    public List<Charm> getCharms() {
        List<Charm> charms = new ArrayList<Charm>();

        for (CharmDot charmDot : db.get().charmStorage.values()) {
            charms.add(charmDot.toCharm());
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

        for (AccountDot accountDot : db.get().accountStorage.values()) {
            if (Objects.equals(accountDot.clientID,clientId)) {
                totalCash += accountDot.money;
            }
        }

        return totalCash;
    }
    private float getMinCash(String clientId) {
        float minCash = -1;

        for (AccountDot acc : db.get().accountStorage.values()) {
            if (Objects.equals(acc.clientID,clientId)) {
                if (minCash == -1) minCash = acc.money; else
                if (acc.money < minCash) minCash = acc.money;
            }
        }

        return minCash;
    }
    private float getMaxCash(String clientId) {
        float maxCash = 0;

        for (AccountDot acc : db.get().accountStorage.values()) {
            if (Objects.equals(acc.clientID,clientId)) {
                if (acc.money > maxCash) maxCash = acc.money;
            }
        }

        return maxCash;
    }

    private String getCharm(String charmID) {
        for (CharmDot charmDot : db.get().charmStorage.values()) {
            if (Objects.equals(charmDot.id, charmID)) {
                return charmDot.name;
            }
        }

        return null;
    }

    public ClientRecord getClientRecord(String clientID) {
        ClientDot client = db.get().clientStorage.get(clientID);

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
