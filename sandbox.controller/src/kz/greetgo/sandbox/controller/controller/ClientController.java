package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ResponseClientListWrapper;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.ArrayList;
import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @NoSecurity
    @Mapping("/get_clients_list")
    public ResponseClientListWrapper getClientsList(@Par("filter") String filter, @Par("sort") String sort,
                                                    @Par("order") String order, @Par("page") String page,
                                                    @Par("size") String size) {
        return clientRegister.get().getClientsList(filter, sort, order, page, size);
    }

    @ToJson
    @NoSecurity
    @Mapping("/add_new_client")
    public void addNewClient(@Par("surname") String surname, @Par("name") String name,
                             @Par("patronymic") String patronymic, @Par("gender") String gender,
                             @Par("birth_date") String birth_date, @Par("charm") int charm,
                             @Par("addrFactStreet") String addrFactStreet, @Par("addrFactHome") String addrFactHome,
                             @Par("addrFactFlat") String addrFactFlat, @Par("addrRegStreet") String addrRegStreet,
                             @Par("addrRegHome") String addrRegHome, @Par("addrRegFlat") String addrRegFlat,
                             @Par("phoneHome") String phoneHome, @Par("phoneWork") String phoneWork,
                             @Par("phoneMob1") String phoneMob1, @Par("phoneMob2") String phoneMob2,
                             @Par("phoneMob3") String phoneMob3
    ) {
        Client client = new Client(-1, surname, name, patronymic,
                gender.equals("MALE") ? Gender.MALE : Gender.FEMALE,
                birth_date, charm);
        ClientAddr addrFact = new ClientAddr(-1, AddrType.FACT, addrFactStreet, addrFactHome, addrFactFlat);
        ClientAddr addrReg = new ClientAddr(-1, AddrType.REG, addrRegStreet, addrRegHome, addrRegFlat);
        ClientPhone p1 = new ClientPhone(-1, phoneHome, PhoneType.HOME);
        ClientPhone p2 = new ClientPhone(-1, phoneWork, PhoneType.WORK);
        ClientPhone p3 = new ClientPhone(-1, phoneMob1, PhoneType.MOBILE);
        ClientPhone p4 = new ClientPhone(-1, phoneMob2, PhoneType.MOBILE);
        ClientPhone p5 = new ClientPhone(-1, phoneMob3, PhoneType.MOBILE);
        clientRegister.get().addNewClient(client,
                new ArrayList<ClientAddr>() {{
                    add(addrFact);
                    add(addrReg);
                }},
                new ArrayList<ClientPhone>() {{
                    add(p1);
                    add(p2);
                    add(p3);
                    add(p4);
                    add(p5);
                }});
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
    public void editClient(@Par("clientId") int clientId,
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
        Client client = new Client(clientId, surname, name, patronymic,
                gender.equals("MALE") ? Gender.MALE : Gender.FEMALE, birth_date, charm);
        ClientAddr addrFact = new ClientAddr(clientId, AddrType.FACT, addrFactStreet, addrFactHome, addrFactFlat);
        ClientAddr addrReg = new ClientAddr(clientId, AddrType.REG, addrRegStreet, addrRegHome, addrRegFlat);
        ClientPhone p1 = new ClientPhone(clientId, phoneHome, PhoneType.HOME);
        ClientPhone p2 = new ClientPhone(clientId, phoneWork, PhoneType.WORK);
        ClientPhone p3 = new ClientPhone(clientId, phoneMob1, PhoneType.MOBILE);
        ClientPhone p4 = new ClientPhone(clientId, phoneMob2, PhoneType.MOBILE);
        ClientPhone p5 = new ClientPhone(clientId, phoneMob3, PhoneType.MOBILE);
        clientRegister.get().editClient(client,
                new ArrayList<ClientAddr>() {{
                    add(addrFact);
                    add(addrReg);
                }},
                new ArrayList<ClientPhone>() {{
                    add(p1);
                    add(p2);
                    add(p3);
                    add(p4);
                    add(p5);
                }});
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_client_info_by_id")
    public ClientInfoResponseTest getClientById(@Par("clientId") String clientId) {
        return clientRegister.get().getClientById(clientId);
    }

}
