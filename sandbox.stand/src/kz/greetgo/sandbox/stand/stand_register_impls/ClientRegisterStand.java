package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.util.*;

@Bean
public class ClientRegisterStand  implements ClientRegister{

    public BeanGetter<StandDb> db;
    private int pageMax = 3;

    @Override
    public ClientRecord addNewClient(ClientToSave clientInfo) {
        CharmDot charmID = db.get().charmStorage.get(clientInfo.charm_id);
        if (charmID == null) {
            throw new RuntimeException("CharmExistenceError");
        }

        String clientID = db.get().addNewClient(clientInfo);

        return getClientRecord(clientID);
    }

    @Override
    public ClientRecord updateClient(ClientToSave clientInfo) {
//        db.get().charmStorage.values().remove(db.get().charmStorage.get(clientInfo.charmID));

        CharmDot charmID = db.get().charmStorage.get(clientInfo.charm_id);
        if (charmID == null) {
            throw new RuntimeException("CharmExistenceError");
        }

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
//        clientDetails.charm = db.get().charmStorage.get(db.get().clientStorage.get(clientID).charmID).name;

        for (AdressDot adressDot : db.get().adressStorage.values()) {
            if (Objects.equals(adressDot.clientID, clientID)) {
                adressDot.toClientDetails(clientDetails);
            }
        }

        for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
            if (phoneDot.clientID == Integer.parseInt(clientID)) {
                phoneDot.toClientDetails(clientDetails);
            }
        }

        return clientDetails;
    }

    @Override
    public ClientToReturn getFilteredClientsInfo(ClientsListParams clientsListParams) {
        String filterStr = clientsListParams.filterSortParams.filterStr;
        String sortBy = clientsListParams.filterSortParams.sortBy;
        String sortOrder = clientsListParams.filterSortParams.sortOrder;
        int pageID = clientsListParams.pageID;

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

        List<ClientRecord> clientRecords = new ArrayList<>();
        for (ClientDot client : clients) {
            clientRecords.add(getClientRecord(String.valueOf(client.id)));
        }
        clientRecords = sort(clientRecords, sortBy, sortOrder);

        clientToReturn.pageCount = getPageNum(clients.size());

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

    @Override
    public List<Charm> getCharms() {
        List<Charm> charms = new ArrayList<Charm>();

        for (CharmDot charmDot : db.get().charmStorage.values()) {
            charms.add(charmDot.toCharm());
        }

        return charms;
    }

    @Override
    public void genClientListReport(ClientsListReportParams clientsListReportParams) {

    }

    @Override
    public int saveReportParams(ReportParamsToSave reportParamsToSave) {
        return 0;
    }

    @Override
    public ReportParamsToSave popReportParams(int report_id) {
        return null;
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

    private float getTotalCash(int clientId) {
        float totalCash = 0;

        for (AccountDot accountDot : db.get().accountStorage.values()) {
            if (accountDot.clientID == clientId) {
                totalCash += accountDot.money;
            }
        }

        return totalCash;
    }
    private float getMinCash(int clientId) {
        float minCash = -1;

        for (AccountDot acc : db.get().accountStorage.values()) {
            if (acc.clientID == clientId) {
                if (minCash == -1) minCash = acc.money; else
                if (acc.money < minCash) minCash = acc.money;
            }
        }

        if (minCash == -1) minCash = 0;
        return minCash;
    }
    private float getMaxCash(int clientId) {
        float maxCash = 0;

        for (AccountDot acc : db.get().accountStorage.values()) {
            if (acc.clientID == clientId) {
                if (acc.money > maxCash) maxCash = acc.money;
            }
        }

        return maxCash;
    }

    private String getCharm(int charmID) {
        for (CharmDot charmDot : db.get().charmStorage.values()) {
            if (charmDot.id == charmID) {
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
        clientInfo.charm = getCharm(client.charm_id);

        return clientInfo;
    }

    private List<ClientRecord> sort(List<ClientRecord> clients, String sortBy, String sortOrder) {
        if ("fio".equals(sortBy)) {
            clients = sortByFIO(clients, sortOrder);
        }
        if ("age".equals(sortBy)) {
            clients = sortByAge(clients, sortOrder);
        }
        if ("totalCash".equals(sortBy)) {
            clients = sortByTotalCash(clients, sortOrder);
        }
        if ("maxCash".equals(sortBy)) {
            clients = sortByMaxCash(clients, sortOrder);
        }
        if ("minCash".equals(sortBy)) {
            clients = sortByMinCash(clients, sortOrder);
        }

        return clients;
    }
    private List<ClientRecord> sortByFIO(List<ClientRecord> clientsList, String sortOrder) {
        if ("up".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    return client1.fio.compareTo(client2.fio);
                }
            });
        } else {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    return client2.fio.compareTo(client1.fio);
                }
            });
        }

        return clientsList;
    }
    private List<ClientRecord> sortByAge(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.age < client2.age) return 1; else
                    if (client1.age == client2.age) return 0; else return -1;
                }
            });
        } else {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.age > client2.age) return 1; else
                    if (client1.age == client2.age) return 0; else return -1;
                }
            });
        }

        return clientsList;
    }
    private List<ClientRecord> sortByTotalCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.totalCash < client2.totalCash) return 1; else
                    if (client1.totalCash == client2.totalCash) return 0; else return -1;
                }
            });
        } else {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.totalCash > client2.totalCash) return 1; else
                    if (client1.totalCash == client2.totalCash) return 0; else return -1;
                }
            });
        }

        return clientsList;
    }
    private List<ClientRecord> sortByMinCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.minCash < client2.minCash) return 1; else
                    if (client1.minCash == client2.minCash) return 0; else return -1;
                }
            });
        } else {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.minCash > client2.minCash) return 1; else
                    if (client1.minCash == client2.minCash) return 0; else return -1;
                }
            });
        }

        return clientsList;
    }
    private List<ClientRecord> sortByMaxCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.maxCash < client2.maxCash) return 1; else
                    if (client1.maxCash == client2.maxCash) return 0; else return -1;
                }
            });
        } else {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.maxCash > client2.maxCash) return 1; else
                    if (client1.maxCash == client2.maxCash) return 0; else return -1;
                }
            });
        }

        return clientsList;
    }
}
