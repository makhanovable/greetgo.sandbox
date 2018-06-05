package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordWrapper;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @NoSecurity
    @Mapping("/get_clients_list") // TODO rename
    public ClientRecordWrapper getClientRecords(@Par("filter") String filter, @Par("sort") String sort,
                                                @Par("order") String order, @Par("page") String page,
                                                @Par("size") String size) {
        Options options = new Options();
        options.filter = filter;
        options.sort = sort;
        options.order = order;
        options.page = page;
        options.size = size;
        return clientRegister.get().getClientRecords(options);
    }

    @ToJson
    @NoSecurity
    @Mapping("/add_new_client")
    public ClientRecord addNewClientRecord(@Par("surname") String surname, @Par("name") String name,
                                           @Par("patronymic") String patronymic, @Par("gender") String gender,
                                           @Par("birth_date") String birth_date, @Par("charm") int charm,
                                           @Par("addrFactStreet") String addrFactStreet, @Par("addrFactHome") String addrFactHome,
                                           @Par("addrFactFlat") String addrFactFlat, @Par("addrRegStreet") String addrRegStreet,
                                           @Par("addrRegHome") String addrRegHome, @Par("addrRegFlat") String addrRegFlat,
                                           @Par("phoneHome") String phoneHome, @Par("phoneWork") String phoneWork,
                                           @Par("phoneMob1") String phoneMob1, @Par("phoneMob2") String phoneMob2,
                                           @Par("phoneMob3") String phoneMob3
    ) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.name = name;
        clientDetails.surname = surname;
        clientDetails.patronymic = patronymic;
        clientDetails.gender = gender;
        clientDetails.birth_date = birth_date;
        clientDetails.charm = charm;
        clientDetails.addrFactStreet = addrFactStreet;
        clientDetails.addrFactHome = addrFactHome;
        clientDetails.addrFactFlat = addrFactFlat;
        clientDetails.addrRegStreet = addrRegStreet;
        clientDetails.addrRegHome = addrRegHome;
        clientDetails.addrRegFlat = addrRegFlat;
        clientDetails.phoneHome = phoneHome;
        clientDetails.phoneWork = phoneWork;
        clientDetails.phoneMob1 = phoneMob1;
        clientDetails.phoneMob2 = phoneMob2;
        clientDetails.phoneMob3 = phoneMob3;
        return clientRegister.get().addNewClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/del_client")
    public void deleteClient(@Par("clientId") int clientId) {
        clientRegister.get().deleteClient(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/edit_client")
    public ClientRecord editClient(@Par("clientId") int clientId,
                                   @Par("surname") String surname, @Par("name") String name,
                                   @Par("patronymic") String patronymic, @Par("gender") String gender,
                                   @Par("birth_date") String birth_date, @Par("charm") int charm,
                                   @Par("addrFactStreet") String addrFactStreet, @Par("addrFactHome") String addrFactHome,
                                   @Par("addrFactFlat") String addrFactFlat, @Par("addrRegStreet") String addrRegStreet,
                                   @Par("addrRegHome") String addrRegHome, @Par("addrRegFlat") String addrRegFlat,
                                   @Par("phoneHome") String phoneHome, @Par("phoneWork") String phoneWork,
                                   @Par("phoneMob1") String phoneMob1, @Par("phoneMob2") String phoneMob2,
                                   @Par("phoneMob3") String phoneMob3
    ) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.id = clientId;
        clientDetails.name = name;
        clientDetails.surname = surname;
        clientDetails.patronymic = patronymic;
        clientDetails.gender = gender;
        clientDetails.birth_date = birth_date;
        clientDetails.charm = charm;
        clientDetails.addrFactStreet = addrFactStreet;
        clientDetails.addrFactHome = addrFactHome;
        clientDetails.addrFactFlat = addrFactFlat;
        clientDetails.addrRegStreet = addrRegStreet;
        clientDetails.addrRegHome = addrRegHome;
        clientDetails.addrRegFlat = addrRegFlat;
        clientDetails.phoneHome = phoneHome;
        clientDetails.phoneWork = phoneWork;
        clientDetails.phoneMob1 = phoneMob1;
        clientDetails.phoneMob2 = phoneMob2;
        clientDetails.phoneMob3 = phoneMob3;
        return clientRegister.get().editClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_client_info_by_id")
    public ClientDetails getClientById(@Par("clientId") int clientId) {
        return clientRegister.get().getClientById(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_charms")
    public List<Charm> getCharms() {
        return clientRegister.get().getCharms();
    }

}
