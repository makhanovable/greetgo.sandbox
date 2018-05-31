package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTest;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

    public BeanGetter<ClientStandDb> db;

    @Override
    public List<ClientResponseTest> getClientsList() {
        List<ClientResponseTest> list = new ArrayList<>();
        for (ClientDot dot : db.get().clientStorage.values()) {
            ClientResponseTest clients = new ClientResponseTest();
            clients.id = dot.id;
            clients.name = dot.name;
            clients.charm = dot.charm;
            clients.age = dot.age;
            clients.total = dot.total;
            clients.max = dot.max;
            clients.min = dot.min;
            list.add(clients);
        }
        return list;
    }

    @Override
    public void addNewClient(String surname, String name, String patronymic, String gender,
                               String birth_date, String charm, String addrFactStreet,
                               String addrFactHome, String addrFactFlat, String addrRegStreet,
                               String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork,
                               String phoneMob1, String phoneMob2, String phoneMob3) {
        db.get().insert(surname, name, patronymic, gender,
                birth_date, charm, addrFactStreet,
                addrFactHome, addrFactFlat, addrRegStreet,
                addrRegHome, addrRegFlat, phoneHome, phoneWork,
                phoneMob1, phoneMob2, phoneMob3);// TODO all
    }
}
