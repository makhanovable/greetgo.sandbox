package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTest;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @NoSecurity
    @Mapping("/get_all_clients")
    public List<ClientResponseTest> getAllClients() {
        return clientRegister.get().getClientsList();
    }

    @ToJson
    @NoSecurity
    @Mapping("/add_new_client")
    public void addNewClient(@Par("surname") String surname, @Par("name") String name,
                             @Par("patronymic") String patronymic, @Par("gender") String gender,
                             @Par("birth_date") String birth_date, @Par("charm") String charm,
                             @Par("addrFactStreet") String addrFactStreet, @Par("addrFactHome") String addrFactHome,
                             @Par("addrFactFlat") String addrFactFlat, @Par("addrRegStreet") String addrRegStreet,
                             @Par("addrRegHome") String addrRegHome, @Par("addrRegFlat") String addrRegFlat,
                             @Par("phoneHome") String phoneHome, @Par("phoneWork") String phoneWork,
                             @Par("phoneMob1") String phoneMob1, @Par("phoneMob2") String phoneMob2,
                             @Par("phoneMob3") String phoneMob3
    ) {
        clientRegister.get().addNewClient(surname, name, patronymic, gender, birth_date, charm,
                addrFactStreet, addrFactHome, addrFactFlat, addrRegStreet, addrRegHome, addrRegFlat,
                phoneHome, phoneWork, phoneMob1, phoneMob2, phoneMob3);
    }

    @ToJson
    @NoSecurity
    @Mapping("/del_client")
    public void delClient(@Par("clientId") String clientId) {
        clientRegister.get().delClient(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/edit_client")
    public void editClient(@Par("clientId") String clientId,
                           @Par("surname") String surname, @Par("name") String name,
                           @Par("patronymic") String patronymic, @Par("gender") String gender,
                           @Par("birth_date") String birth_date, @Par("charm") String charm,
                           @Par("addrFactStreet") String addrFactStreet, @Par("addrFactHome") String addrFactHome,
                           @Par("addrFactFlat") String addrFactFlat, @Par("addrRegStreet") String addrRegStreet,
                           @Par("addrRegHome") String addrRegHome, @Par("addrRegFlat") String addrRegFlat,
                           @Par("phoneHome") String phoneHome, @Par("phoneWork") String phoneWork,
                           @Par("phoneMob1") String phoneMob1, @Par("phoneMob2") String phoneMob2,
                           @Par("phoneMob3") String phoneMob3) {
        clientRegister.get().editClient(clientId, surname, name, patronymic, gender, birth_date, charm,
                addrFactStreet, addrFactHome, addrFactFlat, addrRegStreet, addrRegHome, addrRegFlat,
                phoneHome, phoneWork, phoneMob1, phoneMob2, phoneMob3);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_client_info_by_id")
    public ClientInfoResponseTest getClientById(@Par("clientId") String clientId) {
        return clientRegister.get().getClientById(clientId);
    }

}
