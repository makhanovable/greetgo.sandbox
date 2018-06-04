package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.register.model.ClientInfoResponseTest;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientInfoDot;

import java.util.*;

@Bean
public class ClientStandDb implements HasAfterInject {
    public List<ClientDot> clientStorage = new ArrayList<>();
    public List<ClientInfoDot> clientInfoDotStorage = new ArrayList<>();

    @Override
    public void afterInject() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String[] split = new String[23];
            for (int j = 0; j < 23; j++) {
                split[j] = Integer.toString(random.nextInt(5000));
            }
            split[0] = Integer.toString(i);
            appendPerson(split);
        }
    }

    @SuppressWarnings("unused")
    private void appendPerson(String[] splitLine) {
        ClientDot c = new ClientDot();
        c.name = splitLine[0].trim();
        c.charm = splitLine[1].trim();
        c.age = Integer.parseInt(splitLine[2].trim());
        c.total = Integer.parseInt(splitLine[3].trim());
        c.max = Integer.parseInt(splitLine[4].trim());
        c.min = Integer.parseInt(splitLine[5].trim());
        c.id = Integer.toString(clientStorage.size());
        clientStorage.add(c);


        ClientInfoDot clientInfoDot = new ClientInfoDot();
        clientInfoDot.name = splitLine[6].trim();
        clientInfoDot.surname = splitLine[7].trim();
        clientInfoDot.patronymic = splitLine[8].trim();
        clientInfoDot.gender = splitLine[9].trim();
        clientInfoDot.birth_date = splitLine[10].trim();
        clientInfoDot.charm = splitLine[11].trim();
        clientInfoDot.addrFactStreet = splitLine[12].trim();
        clientInfoDot.addrFactHome = splitLine[13].trim();
        clientInfoDot.addrFactFlat = splitLine[14].trim();
        clientInfoDot.addrRegStreet = splitLine[15].trim();
        clientInfoDot.addrRegHome = splitLine[16].trim();
        clientInfoDot.addrRegFlat = splitLine[17].trim();
        clientInfoDot.phoneHome = splitLine[18].trim();
        clientInfoDot.phoneWork = splitLine[19].trim();
        clientInfoDot.phoneMob1 = splitLine[20].trim();
        clientInfoDot.phoneMob2 = splitLine[21].trim();
        clientInfoDot.phoneMob3 = splitLine[22].trim();
        clientInfoDot.clientId = Integer.toString(clientInfoDotStorage.size());
        clientInfoDotStorage.add(clientInfoDot);
    }

    public void insert(
            String surname, String name, String patronymic, String gender,
            String birth_date, String charm, String addrFactStreet,
            String addrFactHome, String addrFactFlat, String addrRegStreet,
            String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork,
            String phoneMob1, String phoneMob2, String phoneMob3
    ) {
        Random random = new Random();
        String[] split = new String[23];
        split[0] = surname + " " + name + " " + patronymic;
        split[1] = charm + "";
        split[2] = birth_date + "";
        split[3] = Integer.toString(random.nextInt(500000));
        split[4] = Integer.toString(random.nextInt(500000));
        split[5] = Integer.toString(random.nextInt(500000));

        split[6] = name + "";
        split[7] = surname + "";
        split[8] = patronymic + "";
        split[9] = gender + "";
        split[10] = birth_date + "";
        split[11] = charm + "";
        split[12] = addrFactStreet + "";
        split[13] = addrFactHome + "";
        split[14] = addrFactFlat + "";
        split[15] = addrRegStreet + "";
        split[16] = addrRegHome + "";
        split[17] = addrRegFlat + "";
        split[18] = phoneHome + "";
        split[19] = phoneWork + "";
        split[20] = phoneMob1 + "";
        split[21] = phoneMob2 + "";
        split[22] = phoneMob3 + "";
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
        String[] split = new String[23];
        split[0] = surname + " " + name + " " + patronymic;
        split[1] = charm + "";
        split[2] = birth_date + "";
        split[3] = Integer.toString(random.nextInt(500000));
        split[4] = Integer.toString(random.nextInt(500000));
        split[5] = Integer.toString(random.nextInt(500000));

        split[6] = name + "";
        split[7] = surname + "";
        split[8] = patronymic + "";
        split[9] = gender + "";
        split[10] = birth_date + "";
        split[11] = charm + "";
        split[12] = addrFactStreet + "";
        split[13] = addrFactHome + "";
        split[14] = addrFactFlat + "";
        split[15] = addrRegStreet + "";
        split[16] = addrRegHome + "";
        split[17] = addrRegFlat + "";
        split[18] = phoneHome + "";
        split[19] = phoneWork + "";
        split[20] = phoneMob1 + "";
        split[21] = phoneMob2 + "";
        split[22] = phoneMob3 + "";
        clientStorage.set(Integer.parseInt(clientId), createUser(split, clientId));
        clientInfoDotStorage.set(Integer.parseInt(clientId), createUserInfo(split, clientId));
    }

    private ClientDot createUser(String[] splitLine, String clientId) {
        ClientDot c = new ClientDot();
        c.name = splitLine[0].trim();
        c.charm = splitLine[1].trim();
        c.age = Integer.parseInt(splitLine[2].trim());
        c.total = Integer.parseInt(splitLine[3].trim());
        c.max = Integer.parseInt(splitLine[4].trim());
        c.min = Integer.parseInt(splitLine[5].trim());
        c.id = clientId;
        return c;
    }

    private ClientInfoDot createUserInfo(String[] splitLine, String clientId) {
        ClientInfoDot clientInfoDot = new ClientInfoDot();
        clientInfoDot.name = splitLine[6].trim();
        clientInfoDot.surname = splitLine[7].trim();
        clientInfoDot.patronymic = splitLine[8].trim();
        clientInfoDot.gender = splitLine[9].trim();
        clientInfoDot.birth_date = splitLine[10].trim();
        clientInfoDot.charm = splitLine[11].trim();
        clientInfoDot.addrFactStreet = splitLine[12].trim();
        clientInfoDot.addrFactHome = splitLine[13].trim();
        clientInfoDot.addrFactFlat = splitLine[14].trim();
        clientInfoDot.addrRegStreet = splitLine[15].trim();
        clientInfoDot.addrRegHome = splitLine[16].trim();
        clientInfoDot.addrRegFlat = splitLine[17].trim();
        clientInfoDot.phoneHome = splitLine[18].trim();
        clientInfoDot.phoneWork = splitLine[19].trim();
        clientInfoDot.phoneMob1 = splitLine[20].trim();
        clientInfoDot.phoneMob2 = splitLine[21].trim();
        clientInfoDot.phoneMob3 = splitLine[22].trim();
        clientInfoDot.clientId = clientId;
        return clientInfoDot;
    }

    private void correct() {
        for (int i = 0; i < clientStorage.size(); i++) {
            clientStorage.set(i, changeIndex(clientStorage.get(i), i));
        }
        for (int i = 0; i < clientInfoDotStorage.size(); i++) {
            clientInfoDotStorage.set(i, changeIndex(clientInfoDotStorage.get(i), i));
        }
    }

    private ClientDot changeIndex(ClientDot dot, int index) {
        dot.id = Integer.toString(index);
        return dot;
    }

    private ClientInfoDot changeIndex(ClientInfoDot dot, int index) {
        dot.clientId = Integer.toString(index);
        return dot;
    }


}
