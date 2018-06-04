package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ResponseClientListWrapper;

import java.util.List;

public interface ClientRegister {

    ResponseClientListWrapper getClientsList(String filter, String sort, String order,
                                             String page, String size);

    void addNewClient(Client client, List<ClientAddr> addrs, List<ClientPhone> phones);

    void delClient(String clientId);

    void editClient(Client client, List<ClientAddr> addrs, List<ClientPhone> phones);

    ClientInfoResponseTest getClientById(String clientId);
}
