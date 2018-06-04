package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTestWrapper;

import java.util.List;

public interface ClientRegister {
    ClientResponseTestWrapper getClientsList(String filter, String sort, String order,
                                             String page, String size);

    void addNewClient(String surname, String name, String patronymic, String gender,
                      String birth_date, String charm, String addrFactStreet,
                      String addrFactHome, String addrFactFlat, String addrRegStreet,
                      String addrRegHome, String addrRegFlat, String phoneHome,
                      String phoneWork, String phoneMob1, String phoneMob2, String phoneMob3);

    void delClient(String clientId);

    void editClient(String clientId, String surname, String name, String patronymic, String gender,
                    String birth_date, String charm, String addrFactStreet,
                    String addrFactHome, String addrFactFlat, String addrRegStreet,
                    String addrRegHome, String addrRegFlat, String phoneHome,
                    String phoneWork, String phoneMob1, String phoneMob2, String phoneMob3);

    ClientInfoResponseTest getClientById(String clientId);
}
