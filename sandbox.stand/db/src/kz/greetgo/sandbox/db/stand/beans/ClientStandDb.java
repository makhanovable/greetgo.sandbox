package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDetailsDot;
import kz.greetgo.sandbox.db.stand.model.ClientRecordDot;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Bean
public class ClientStandDb implements HasAfterInject {

    private SecureRandom random = new SecureRandom();
    private String[] genders = new String[]{"MALE", "FEMALE"};

    private List<ClientRecordDot> clientRecordStorage = new ArrayList<>();
    private List<ClientDetailsDot> clientDetailsStorage = new ArrayList<>();
    public List<CharmDot> charmsStorage = new ArrayList<>();
    public List<ClientRecordDot> out;
    private int ID;

    @Override
    public void afterInject() {
        for (int i = 0; i < 50; i++) {
            CharmDot charm = new CharmDot();
            charm.id = i;
            charm.name = randomString();
            charmsStorage.add(charm);
        }

        for (int i = 0; i < 100; i++) {
            ClientDetailsDot clientDetailsDot = new ClientDetailsDot();
            ClientRecordDot clientRecordDot = new ClientRecordDot();

            clientDetailsDot.id = i;
            clientDetailsDot.name = randomString();
            clientDetailsDot.surname = randomString();
            clientDetailsDot.patronymic = randomString();
            clientDetailsDot.gender = genders[random.nextInt(2)];
            clientDetailsDot.birth_date = "Thu Dec 14 2006";
            clientDetailsDot.charm = random.nextInt(charmsStorage.size());
            clientDetailsDot.addrFactStreet = randomString();
            clientDetailsDot.addrFactHome = Integer.toString(random.nextInt(100));
            clientDetailsDot.addrFactFlat = Integer.toString(random.nextInt(100));
            clientDetailsDot.addrRegStreet = randomString();
            clientDetailsDot.addrRegHome = Integer.toString(random.nextInt(100));
            clientDetailsDot.addrRegFlat = Integer.toString(random.nextInt(100));
            ClientPhone[] phones = new ClientPhone[5];
            for (int j = 0; j < 5; j++) {
                phones[j] = new ClientPhone();
                phones[j].number = random.nextInt(10) + "" + (random.nextInt(899) + 100);
                phones[j].type = PhoneType.MOBILE;
            }
            clientDetailsDot.phones = phones;
            clientDetailsStorage.add(clientDetailsDot);

            clientRecordDot.id = i;
            clientRecordDot.name = clientDetailsDot.surname + " " + clientDetailsDot.name
                    + " " + clientDetailsDot.patronymic;
            clientRecordDot.charm = getCharmById(clientDetailsDot.charm);
            clientRecordDot.age = calculateAge(clientDetailsDot.birth_date);
            clientRecordDot.total = (float) 1.3 * random.nextInt(5000);
            clientRecordDot.max = (float) 1.3 * random.nextInt(5000);
            clientRecordDot.min = (float) 1.3 * random.nextInt(5000);
            clientRecordStorage.add(clientRecordDot);
        }
        ID = clientRecordStorage.size();
    }

    public List<ClientRecordDot> getClientRecordStorage(Options options) {
        List<ClientRecordDot> returned = new ArrayList<>();
        out = new ArrayList<>();

        System.out.println(options.filter + " - " + options.sort
                + " - " + options.order + " - " + options.page + " - " + options.size);

        if (options.filter != null && !options.filter.isEmpty()) {
            for (ClientRecordDot aList : clientRecordStorage) {
                String name = aList.name.replace(" ", "").toLowerCase();
                if (name.matches("(?i).*" + options.filter.replace(" ", "")
                        .toLowerCase() + ".*"))
                    out.add(aList);
            }
        } else
            out = clientRecordStorage;

        try {
            if (options.sort != null && options.order != null &&
                     !options.order.isEmpty()) {
                switch (options.sort) {
                    case name:
                        out.sort(Comparator.comparing(o -> o.name));
                        break;
                    case age:
                        out.sort(Comparator.comparing(o -> o.age));
                        break;
                    case total:
                        out.sort(Comparator.comparing(o -> o.total));
                        break;
                    case max:
                        out.sort(Comparator.comparing(o -> o.max));
                        break;
                    case min:
                        out.sort(Comparator.comparing(o -> o.min));
                        break;
                }
                if (options.order.equals("desc"))
                    Collections.reverse(out);
            }
        } catch (Exception ex) {
            System.out.println("ConcurrentModificationException");
        }

        int number, size;
        if (options.page == null)
            number = 0;
        else
            number = Integer.parseInt(options.page);
        if (options.size == null)
            size = 0;
        else
            size = Integer.parseInt(options.size);
        int start = number * size;
        for (int i = 0; i < size; i++) {
            try {
                returned.add(out.get(start));
                start++;
            } catch (Exception ex) {
                break;
            }
        }
        return returned;
    }


    public List<ClientRecordDot> getClientRecordStorage(String filter) {
        out = new ArrayList<>();

        System.out.println(filter);

        if (filter != null && !filter.isEmpty()) {
            for (ClientRecordDot aList : clientRecordStorage) {
                String name = aList.name.replace(" ", "").toLowerCase();
                if (name.matches("(?i).*" + filter.replace(" ", "")
                        .toLowerCase() + ".*"))
                    out.add(aList);
            }
        } else
            out = clientRecordStorage;

        return out;
    }

    public void deleteClientInfo(int id) {
        System.out.println("deleting id = " + id);
        for (int i = 0; i < clientDetailsStorage.size(); i++) {
            if (clientDetailsStorage.get(i).id == id || clientRecordStorage.get(i).id == id) {
                System.out.println("found " + clientDetailsStorage.get(i).name);
                clientDetailsStorage.remove(i);
                clientRecordStorage.remove(i);
                break;
            }
        }
    }

    private ClientRecordDot getDeletingClientRecord(int id) {
        for (ClientRecordDot aClientRecordStorage : clientRecordStorage) {
            if (aClientRecordStorage.id == id) {
                return aClientRecordStorage;
            }
        }
        return null;
    }

    public ClientRecordDot addNewClientRecord(ClientDetails details) {
        int id = ID;
        ID++;
        ClientDetailsDot clientDetailsDot = new ClientDetailsDot();
        ClientRecordDot clientRecordDot = new ClientRecordDot();

        clientDetailsDot.id = id;
        clientDetailsDot.name = details.name;
        clientDetailsDot.surname = details.surname;
        if (details.patronymic != null) clientDetailsDot.patronymic = details.patronymic;
        else clientDetailsDot.patronymic = "";
        clientDetailsDot.gender = details.gender;
        clientDetailsDot.birth_date = details.birth_date;
        clientDetailsDot.charm = details.charm;
        if (details.addrFactStreet != null) clientDetailsDot.addrFactStreet = details.addrFactStreet;
        else clientDetailsDot.addrFactStreet = "";
        if (details.addrFactHome != null) clientDetailsDot.addrFactHome = details.addrFactHome;
        else clientDetailsDot.addrFactHome = "";
        if (details.addrFactFlat != null) clientDetailsDot.addrFactFlat = details.addrFactFlat;
        else clientDetailsDot.addrFactFlat = "";
        clientDetailsDot.addrRegStreet = details.addrRegStreet;
        clientDetailsDot.addrRegHome = details.addrRegHome;
        clientDetailsDot.addrRegFlat = details.addrRegFlat;
        ClientPhone[] phones = new ClientPhone[5];
        for (int i = 0; i < details.phones.length; i++) {
            phones[i] = new ClientPhone();
            ClientPhone phone = details.phones[i];
            if (phone.number != null) phones[i].number = phone.number;
            else phones[i].number = "";
            phones[i].type = phone.type;
        }
        clientDetailsDot.phones = phones;
        clientDetailsStorage.add(clientDetailsDot);

        clientRecordDot.id = id;
        clientRecordDot.name = clientDetailsDot.surname + " " + clientDetailsDot.name
                + " " + clientDetailsDot.patronymic;
        clientRecordDot.charm = getCharmById(clientDetailsDot.charm);
        clientRecordDot.age = calculateAge(clientDetailsDot.birth_date);
        clientRecordDot.total = 0;
        clientRecordDot.max = 0;
        clientRecordDot.min = 0;
        clientRecordStorage.add(clientRecordDot);

        return clientRecordDot;
    }

    public ClientRecordDot editClientRecord(ClientDetails details) {
        ClientDetailsDot clientDetailsDot = new ClientDetailsDot();
        ClientRecordDot clientRecordDot = new ClientRecordDot();

        clientDetailsDot.id = details.id;
        clientDetailsDot.name = details.name;
        clientDetailsDot.surname = details.surname;
        if (details.patronymic != null) clientDetailsDot.patronymic = details.patronymic;
        else clientDetailsDot.patronymic = "";
        clientDetailsDot.gender = details.gender;
        clientDetailsDot.birth_date = details.birth_date;
        clientDetailsDot.charm = details.charm;
        if (details.addrFactStreet != null) clientDetailsDot.addrFactStreet = details.addrFactStreet;
        else clientDetailsDot.addrFactStreet = "";
        if (details.addrFactHome != null) clientDetailsDot.addrFactHome = details.addrFactHome;
        else clientDetailsDot.addrFactHome = "";
        if (details.addrFactFlat != null) clientDetailsDot.addrFactFlat = details.addrFactFlat;
        else clientDetailsDot.addrFactFlat = "";
        clientDetailsDot.addrRegStreet = details.addrRegStreet;
        clientDetailsDot.addrRegHome = details.addrRegHome;
        clientDetailsDot.addrRegFlat = details.addrRegFlat;
        ClientPhone[] phones = new ClientPhone[5];
        for (int i = 0; i < details.phones.length; i++) {
            phones[i] = new ClientPhone();
            ClientPhone phone = details.phones[i];
            if (phone.number != null) phones[i].number = phone.number;
            else phones[i].number = "";
            phones[i].type = phone.type;
        }
        clientDetailsDot.phones = phones;

        clientRecordDot.id = details.id;
        clientRecordDot.name = clientDetailsDot.surname + " " + clientDetailsDot.name
                + " " + clientDetailsDot.patronymic;
        clientRecordDot.charm = getCharmById(clientDetailsDot.charm);
        clientRecordDot.age = calculateAge(clientDetailsDot.birth_date);

        ClientRecordDot deletingClientRecord = getDeletingClientRecord(details.id);

        assert deletingClientRecord != null;
        clientRecordDot.total = deletingClientRecord.total;
        clientRecordDot.max = deletingClientRecord.max;
        clientRecordDot.min = deletingClientRecord.min;
        deleteClientInfo(details.id);
        clientRecordStorage.add(clientRecordDot);
        clientDetailsStorage.add(clientDetailsDot);

        return clientRecordDot;
    }

    public ClientDetailsDot getClientDetailById(int clientId) {
        ClientDetailsDot dot = null;
        for (ClientDetailsDot aClientDetailsStorage : clientDetailsStorage) {
            if (aClientDetailsStorage.id == clientId) {
                dot = aClientDetailsStorage;
                break;
            }
        }
        return dot;
    }

    private String randomString() {
        String AB = "abcdefghijklmnopqrstuvwxyz";
        int len = 7;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return (AB.charAt(random.nextInt(AB.length())) + "").toUpperCase()
                + sb.toString();
    }

    private int calculateAge(String date) {
        System.out.println("have to calculate = " + date);
        try {
            String[] split = date.split(" ");
            DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
            int year = Integer.parseInt(split[3]);
            int month = getMonthNumber(split[1]);
            int day = Integer.parseInt(split[2]);

            Date input = new Date();
            Date birth = formatter.parse(month + "/" + day + "/" + year);
            LocalDate birthDate = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if ((currentDate != null)) {
                return Period.between(birthDate, currentDate).getYears();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private String getCharmById(int id) {
        try {
            for (CharmDot aCharmsStorage : charmsStorage) {
                if (aCharmsStorage.id == id) {
                    return aCharmsStorage.name;
                }
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    private int getMonthNumber(String monthName) {
        String[] list = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
        for (int i = 0; i < list.length; i++) {
            if (list[i].toLowerCase().equals(monthName.toLowerCase()))
                return i + 1;
        }
        return 0;
    }

}
