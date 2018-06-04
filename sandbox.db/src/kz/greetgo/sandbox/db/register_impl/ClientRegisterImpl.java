package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTestWrapper;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {


    @Override
    public ClientResponseTestWrapper getClientsList(String filter, String sort, String order, String page, String size) {
        return null;
    }

    @Override
    public void addNewClient(String surname, String name, String patronymic, String gender, String birth_date, String charm, String addrFactStreet, String addrFactHome, String addrFactFlat, String addrRegStreet, String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork, String phoneMob1, String phoneMob2, String phoneMob3) {

    }

    @Override
    public void delClient(String clientId) {

    }

    @Override
    public void editClient(String clientId, String surname, String name, String patronymic, String gender, String birth_date, String charm, String addrFactStreet, String addrFactHome, String addrFactFlat, String addrRegStreet, String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork, String phoneMob1, String phoneMob2, String phoneMob3) {

    }

    @Override
    public ClientInfoResponseTest getClientById(String clientId) {
        return null;
    }

}
