package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTest;
import kz.greetgo.sandbox.controller.register.model.ClientResponseTestWrapper;
import kz.greetgo.sandbox.db.stand.beans.ClientStandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientInfoDot;

import java.util.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

    public BeanGetter<ClientStandDb> db;

    @Override
    public ClientResponseTestWrapper getClientsList(String filter, String sort, String order,
                                                    String pageNumber, String pageSize) {
        ClientResponseTestWrapper wrapper = new ClientResponseTestWrapper();
        List<ClientResponseTest> list = new ArrayList<>();
        List<ClientResponseTest> out = new ArrayList<>();
        List<ClientResponseTest> filtered = new ArrayList<>();
        for (ClientDot dot : db.get().clientStorage) {
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
        System.out.println(filter + " - " + sort + " - " + order + " - " + pageNumber + " - " + pageSize);

        if (filter != null && !filter.isEmpty()) {
            for (ClientResponseTest aList : list) {
                String name = aList.name.replace(" ", "").toLowerCase();
                if (name.matches("(?i).*" + filter.toLowerCase() + ".*"))
                    filtered.add(aList);
            }
        } else
            filtered = list;

        if (sort != null && order != null && !sort.isEmpty() && !order.isEmpty()) {
            switch (sort) {
                case "name":
                    filtered.sort(Comparator.comparing(o -> o.name));
                    break;
                case "age":
                    filtered.sort(Comparator.comparing(o -> o.age));
                    break;
                case "total":
                    filtered.sort(Comparator.comparing(o -> o.total));
                    break;
                case "max":
                    filtered.sort(Comparator.comparing(o -> o.max));
                    break;
                case "min":
                    filtered.sort(Comparator.comparing(o -> o.min));
                    break;
            }
            if (order.equals("desc"))
                Collections.reverse(filtered);
        }

        int number, size;
        if (pageNumber == null)
            number = 0;
        else
            number = Integer.parseInt(pageNumber);
        if (pageSize == null)
            size = 0;
        else
            size = Integer.parseInt(pageSize);
        int start = number * size;
        for (int i = 0; i < size; i++) {
            try {
                out.add(filtered.get(start));
                start++;
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }

        wrapper.total_count = filtered.size();
        wrapper.items = out;
        return wrapper;
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

    @Override
    public void delClient(String clientId) {
        db.get().remove(clientId);
    }

    @Override
    public void editClient(String clientId, String surname, String name, String patronymic, String gender, String birth_date, String charm, String addrFactStreet, String addrFactHome, String addrFactFlat, String addrRegStreet, String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork, String phoneMob1, String phoneMob2, String phoneMob3) {
        db.get().edit(clientId, surname, name, patronymic, gender,
                birth_date, charm, addrFactStreet,
                addrFactHome, addrFactFlat, addrRegStreet,
                addrRegHome, addrRegFlat, phoneHome, phoneWork,
                phoneMob1, phoneMob2, phoneMob3);
    }

    @Override
    public ClientInfoResponseTest getClientById(String clientId) {
        ClientInfoResponseTest info = new ClientInfoResponseTest();
        System.out.println(clientId);
        ClientInfoDot dot = db.get().clientInfoDotStorage.get(Integer.parseInt(clientId));
        info.name = dot.name;
        info.surname = dot.surname;
        info.patronymic = dot.patronymic;
        info.gender = dot.gender;
        info.birth_date = dot.birth_date;
        info.charm = dot.charm;
        info.addrFactStreet = dot.addrFactStreet;
        info.addrFactHome = dot.addrFactHome;
        info.addrFactFlat = dot.addrFactFlat;
        info.addrRegStreet = dot.addrRegStreet;
        info.addrRegHome = dot.addrRegHome;
        info.addrRegFlat = dot.addrRegFlat;
        info.phoneHome = dot.phoneHome;
        info.phoneWork = dot.phoneWork;
        info.phoneMob1 = dot.phoneMob1;
        info.phoneMob2 = dot.phoneMob2;
        info.phoneMob3 = dot.phoneMob3;
        return info;
    }
}
