package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.security.SecureRandom;
import java.util.*;

@Bean
public class ClientStandDb implements HasAfterInject {
    private SecureRandom random = new SecureRandom();
    public List<ClientDot> clientStorage = new ArrayList<>();

    @Override
    public void afterInject() {
        for (int i = 0; i < 100; i++) {
            ClientDot c = new ClientDot();
            c.name = randomString();
            c.surname = randomString();
            c.patronymic = randomString();
            c.age = random.nextInt(90) + 10;
            c.total = (float) 1.3 * random.nextInt(5000);
            c.max = (float) 1.3 * random.nextInt(5000);
            c.min = (float) 1.3 * random.nextInt(5000);

            c.charm = random.nextInt(10); // TODO edit
            c.gender = randomString(); // TODO edit
            c.birth_date = randomString(); // TODO edit
            c.addrFactStreet = randomString();
            c.addrFactHome = Integer.toString(random.nextInt(100));
            c.addrFactFlat = Integer.toString(random.nextInt(100));

            c.clientId = i + "";

            c.addrRegStreet = randomString();
            c.addrRegHome = Integer.toString(random.nextInt(100));
            c.addrRegFlat = Integer.toString(random.nextInt(100));
            c.phoneHome = "+" + random.nextInt(10) + random.nextInt(899) + 100 + "" + random.nextInt(8999999) + 1000000;
            c.phoneWork = "+" + random.nextInt(10) + random.nextInt(899) + 100 + "" + random.nextInt(8999999) + 1000000;
            c.phoneMob1 = "+" + random.nextInt(10) + random.nextInt(899) + 100 + "" + random.nextInt(8999999) + 1000000;
            c.phoneMob2 = "+" + random.nextInt(10) + random.nextInt(899) + 100 + "" + random.nextInt(8999999) + 1000000;
            c.phoneMob3 = "+" + random.nextInt(10) + random.nextInt(899) + 100 + "" + random.nextInt(8999999) + 1000000;

            appendPerson(c);
        }
    }

    @SuppressWarnings("unused")
    private void appendPerson(ClientDot c) {
        clientStorage.add(c);
    }

    public void insert(Client client, List<ClientAddr> addrs, List<ClientPhone> phones) {
        int id = clientStorage.size();
        ClientDot c = new ClientDot();
        c.name = client.name;
        c.surname = client.surname;
        c.patronymic = client.patronymic;
        c.age = random.nextInt(90) + 10; // TODO calculate age
        c.total = (float) 1.3 * random.nextInt(5000);
        c.max = (float) 1.3 * random.nextInt(5000);
        c.min = (float) 1.3 * random.nextInt(5000);
        c.charm = client.charm; // TODO edit
        c.gender = "MALE"; // TODO edit
        c.birth_date = client.birth_date; // TODO edit
        c.addrFactStreet = addrs.get(0).street;
        c.addrFactHome = addrs.get(0).house;
        c.addrFactFlat = addrs.get(0).flat;
        c.clientId = id + "";
        c.addrRegStreet = addrs.get(1).street;
        c.addrRegHome = addrs.get(1).house;
        c.addrRegFlat = addrs.get(1).flat;
        c.phoneHome = phones.get(0).number;
        c.phoneWork = phones.get(0).number;
        c.phoneMob1 = phones.get(0).number;
        c.phoneMob2 = phones.get(0).number;
        c.phoneMob3 = phones.get(0).number;
        appendPerson(c);
    }

    public void remove(String id) {
        clientStorage.remove(Integer.parseInt(id));
        correct();
    }

    public void edit(Client client, List<ClientAddr> addrs, List<ClientPhone> phones) {
        ClientDot c = new ClientDot();
        c.name = client.name;
        c.surname = client.surname;
        c.patronymic = client.patronymic;
        c.age = random.nextInt(90) + 10; // TODO calculate age
        c.total = (float) 1.3 * random.nextInt(5000);
        c.max = (float) 1.3 * random.nextInt(5000);
        c.min = (float) 1.3 * random.nextInt(5000);
        c.charm = client.charm; // TODO edit
        c.gender = "MALE"; // TODO edit
        c.birth_date = client.birth_date; // TODO edit
        c.addrFactStreet = addrs.get(0).street;
        c.addrFactHome = addrs.get(0).house;
        c.addrFactFlat = addrs.get(0).flat;
        c.clientId = client.id + "";
        c.addrRegStreet = addrs.get(1).street;
        c.addrRegHome = addrs.get(1).house;
        c.addrRegFlat = addrs.get(1).flat;
        c.phoneHome = phones.get(0).number;
        c.phoneWork = phones.get(0).number;
        c.phoneMob1 = phones.get(0).number;
        c.phoneMob2 = phones.get(0).number;
        c.phoneMob3 = phones.get(0).number;

        clientStorage.set(client.id, c);
    }


    private void correct() {
        for (int i = 0; i < clientStorage.size(); i++) {
            clientStorage.set(i, changeIndex(clientStorage.get(i), i));
        }
    }

    private ClientDot changeIndex(ClientDot dot, int index) {
        dot.clientId = Integer.toString(index);
        return dot;
    }

    private String randomString() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int len = 7;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return sb.toString();
    }

}
