package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.*;

@Bean
public class ClientStandDb implements HasAfterInject {
    public List<ClientDot> clientStorage = new ArrayList<>();

    @Override
    public void afterInject() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String[] split = new String[6];
            for (int j = 0; j < 6; j++) {
                split[j] = Integer.toString(random.nextInt(5000));
            }
            appendPerson(split);
        }
    }

    @SuppressWarnings("unused")
    private void appendPerson(String[] splitLine) {
        ClientDot c = new ClientDot();
        c.name = splitLine[0].trim();
        c.charm = splitLine[1].trim();
        c.age = splitLine[2].trim();
        c.total = splitLine[3].trim();
        c.max = splitLine[4].trim();
        c.min = splitLine[5].trim();
        c.id = Integer.toString(clientStorage.size());
        clientStorage.add(c);
    }

    public void insert(
            String surname, String name, String patronymic, String gender,
            String birth_date, String charm, String addrFactStreet,
            String addrFactHome, String addrFactFlat, String addrRegStreet,
            String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork,
            String phoneMob1, String phoneMob2, String phoneMob3
    ) {
        Random random = new Random();
        String[] split = new String[7];
        split[0] = surname + " " + name + " " + patronymic;
        split[1] = charm + "";
        split[2] = birth_date + "";
        split[3] = Integer.toString(random.nextInt(500000));
        split[4] = Integer.toString(random.nextInt(500000));
        split[5] = Integer.toString(random.nextInt(500000));
        appendPerson(split);
    }

    public void remove(String id) {
        clientStorage.remove(Integer.parseInt(id));
        correct();
    }

    public void edit(
            String clientId,
            String surname, String name, String patronymic, String gender,
            String birth_date, String charm, String addrFactStreet,
            String addrFactHome, String addrFactFlat, String addrRegStreet,
            String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork,
            String phoneMob1, String phoneMob2, String phoneMob3

    ) {
        Random random = new Random();
        String[] split = new String[6];
        split[0] = surname + " " + name + " " + patronymic;
        split[1] = charm + "";
        split[2] = birth_date + "";
        split[3] = Integer.toString(random.nextInt(500000));
        split[4] = Integer.toString(random.nextInt(500000));
        split[5] = Integer.toString(random.nextInt(500000));
        clientStorage.set(Integer.parseInt(clientId), createUser(split, clientId));
    }

    private ClientDot createUser(String[] splitLine, String clientId) {
        ClientDot c = new ClientDot();
        c.name = splitLine[0].trim();
        c.charm = splitLine[1].trim();
        c.age = splitLine[2].trim();
        c.total = splitLine[3].trim();
        c.max = splitLine[4].trim();
        c.min = splitLine[5].trim();
        c.id = clientId;
        return c;
    }

    private void correct() {
        for (int i = 0; i < clientStorage.size(); i++) {
            clientStorage.set(i, changeIndex(clientStorage.get(i), i));
        }
    }

    private ClientDot changeIndex(ClientDot dot, int index) {
        dot.id = Integer.toString(index);
        return dot;
    }


}
