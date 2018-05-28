package kz.greetgo.sandbox.db.model;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sorter {
    public static List<ClientRecord> sort(List<ClientRecord> clients, String sortBy, String sortOrder) {
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
    private static List<ClientRecord> sortByFIO(List<ClientRecord> clientsList, String sortOrder) {
        if ("up".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    return client1.fio.compareTo(client2.fio);
                }
            });
        } else
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    return client2.fio.compareTo(client1.fio);
                }
            });
        }

        return clientsList;
    }
    private static List<ClientRecord> sortByAge(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.age < client2.age) return 1; else
                    if (client1.age == client2.age) return 0; else return -1;
                }
            });
        } else
        if ("up".equals(sortOrder)){
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
    private static List<ClientRecord> sortByTotalCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.totalCash < client2.totalCash) return 1; else
                    if (client1.totalCash == client2.totalCash) return 0; else return -1;
                }
            });
        } else
        if ("up".equals(sortOrder)){
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
    private static List<ClientRecord> sortByMinCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.minCash < client2.minCash) return 1; else
                    if (client1.minCash == client2.minCash) return 0; else return -1;
                }
            });
        } else
        if ("up".equals(sortOrder)){
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
    private static List<ClientRecord> sortByMaxCash(List<ClientRecord> clientsList, String sortOrder) {
        if ("down".equals(sortOrder)) {
            Collections.sort(clientsList, new Comparator<ClientRecord>() {
                @Override
                public int compare(ClientRecord client1, ClientRecord client2) {
                    if (client1.maxCash < client2.maxCash) return 1; else
                    if (client1.maxCash == client2.maxCash) return 0; else return -1;
                }
            });
        } else
        if ("up".equals(sortOrder)){
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
