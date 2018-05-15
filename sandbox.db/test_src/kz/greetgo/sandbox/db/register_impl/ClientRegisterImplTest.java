package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.msoffice.docx.Run;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.*;
import kz.greetgo.sandbox.controller.report.model.ClientListRow;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AdressDot;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.*;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

// TODO: TODO: ещё раз проверь все тесты и дополни

//TODO: нужно больше тестов на пагинацию, скажи, когда приступишь. Разбирём все моменты

//TODO: Нет тестов, проверяющих поведение регистров при неверном вводе. Добавить.
public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<CharmTestDao> charmTestDao;
    public BeanGetter<ClientTestDao> clientTestDao;
    public BeanGetter<AccountTestDao> accountTestDao;
    public BeanGetter<PhoneTestDao> phoneTestDao;
    public BeanGetter<AdressTestDao> adressTestDao;
    public BeanGetter<ReportParamsDao> reportParamsDao;
    public BeanGetter<StandDb> standDb;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expectedExceptions = RuntimeException.class)
    public void testEditFormFill() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        adressTestDao.get().clearAdresses();
        phoneTestDao.get().clearPhones();

        CharmDot charmDot = new CharmDot();
        charmDot.id = 1;
        charmDot.name = "меланхолик";
        charmDot.description = "asdasd";
        charmDot.energy = (float) 123;
        charmTestDao.get().insertCharm(charmDot);

        ClientToSave clientToSave = new ClientToSave();
        clientToSave.id = 1;
        clientToSave.name = null;
        clientToSave.surname = null;
        clientToSave.gender = "MALE";
        clientToSave.birth_date = "1997-09-27";
        clientToSave.charm_id = 1;
        clientToSave.rAdressStreet = "Каратал";
        clientToSave.rAdressHouse = "15";
        clientToSave.rAdressFlat = "25";

        //
        //
        ClientRecord clientRecord = clientRegister.get().addNewClient(clientToSave);
        //
        //

//        thrown.expect(RuntimeException.class);

        assertThat(false).isTrue();
    }
    @Test
    //TODO: исправить тест
    public void testAddNewClient() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        adressTestDao.get().clearAdresses();
        phoneTestDao.get().clearPhones();

        CharmDot charmDot = new CharmDot();
        charmDot.id = 1;
        charmDot.name = "меланхолик";
        charmDot.description = "asdasd";
        charmDot.energy = (float) 123;
        charmTestDao.get().insertCharm(charmDot);

        ClientToSave clientToSave = new ClientToSave();
        clientToSave.id = 1;
        clientToSave.name = "Владимир";
        clientToSave.surname = "Путин";
        clientToSave.patronymic = "Владимирович";
        clientToSave.gender = "MALE";
        clientToSave.birth_date = "1997-09-27";
        clientToSave.charm_id = 1;
        clientToSave.mobilePhones.add("87779105332");
        clientToSave.rAdressStreet = "Каратал";
        clientToSave.rAdressHouse = "15";
        clientToSave.rAdressFlat = "25";

        //
        //
        ClientRecord clientRecord = clientRegister.get().addNewClient(clientToSave);
        //
        //

        List<Adress> adresses = adressTestDao.get().getAdress(clientRecord.id);
        List<Phone> phones = phoneTestDao.get().getPhones(clientRecord.id);

        assertThat(clientRecord).isNotNull();
        String fio = clientToSave.surname + " " + clientToSave.name + " " + clientToSave.patronymic;
        assertThat(clientRecord.fio).isEqualTo(fio);
        assertThat(clientRecord.charm).isEqualTo(charmDot.name);
        assertThat(clientRecord.age).isEqualTo(20);
        assertThat(adresses).isNotNull();
        assertThat(adresses).hasSize(1);
        assertThat(adresses.get(0).adressType).isEqualTo("REG");
        assertThat(adresses.get(0).street).isEqualTo(clientToSave.rAdressStreet);
        assertThat(phones).isNotNull();
        assertThat(phones).hasSize(1);
        assertThat(phones.get(0).number).isEqualTo(clientToSave.mobilePhones.get(0));

        List<ClientDot> clients = clientTestDao.get().getAllClients();
        assertThat(clients.get(0).surname).isEqualTo(clientToSave.surname);
        assertThat(clients.get(0).name).isEqualTo(clientToSave.name);
        assertThat(clients.get(0).patronymic).isEqualTo(clientToSave.patronymic);
        assertThat(clients.get(0).birth_date).isEqualTo(clientToSave.birth_date);
        assertThat(clients.get(0).charm_id).isEqualTo(clientToSave.charm_id);
        assertThat(clients.get(0).gender).isEqualTo(clientToSave.gender);
    }

    @Test
    public void testUpdateClient() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        phoneTestDao.get().clearPhones();
        adressTestDao.get().clearAdresses();

        CharmDot charmDot = new CharmDot();
        charmDot.id = 1;
        charmDot.name = "меланхолик";
        charmDot.description = "asdasd";
        charmDot.energy = (float) 123;
        charmTestDao.get().insertCharm(charmDot);

        ClientToSave clientToSave = new ClientToSave();
        clientToSave.name = "Владимир";
        clientToSave.surname = "Путин";
        clientToSave.patronymic = "Владимирович";
        clientToSave.gender = "MALE";
        clientToSave.birth_date = "1997-09-27";
        clientToSave.charm_id = 1;
        clientToSave.mobilePhones.add("87779105332");
        clientToSave.rAdressStreet = "Каратал";
        clientToSave.rAdressHouse = "15";
        clientToSave.rAdressFlat = "25";
        ClientRecord clientRecord1 = clientRegister.get().addNewClient(clientToSave);

        clientToSave.id = clientRecord1.id;
        clientToSave.name = "Александр";
        clientToSave.surname = "Пушкин";
        clientToSave.patronymic = "Сергеевич";
        clientToSave.gender = "MALE";
        clientToSave.birth_date = "1987-09-27";
        clientToSave.charm_id = 1;
        clientToSave.mobilePhones.add("87474415332");
        clientToSave.rAdressStreet = "Каратал";
        clientToSave.rAdressHouse = "15";
        clientToSave.rAdressFlat = "25";
        clientToSave.fAdressStreet = "Кабанбай батыра";
        clientToSave.fAdressHouse = "138";
        clientToSave.fAdressFlat = "9";
        clientToSave.homePhone.add("87282305227");

        //
        //
        ClientRecord clientRecord2  = clientRegister.get().updateClient(clientToSave);
        //
        //

        List<Adress> adresses = adressTestDao.get().getAdress(clientRecord1.id);
        List<Phone> phones = phoneTestDao.get().getPhones(clientRecord1.id);

        assertThat(clientRecord2).isNotNull();
        assertThat(clientRecord2.id).isEqualTo(clientRecord1.id);
        String fio = clientToSave.surname + " " + clientToSave.name + " " + clientToSave.patronymic;
        assertThat(clientRecord2.fio).isEqualTo(fio);
        assertThat(clientRecord2.charm).isEqualTo(charmDot.name);
        assertThat(clientRecord2.age).isEqualTo(30);
        assertThat(adresses).isNotNull();
        assertThat(adresses).hasSize(2);
        assertThat(adresses.get(0).adressType).isEqualTo("REG");
        assertThat(adresses.get(0).street).isEqualTo(clientToSave.rAdressStreet);
        assertThat(adresses.get(1).adressType).isEqualTo("FACT");
        assertThat(adresses.get(1).street).isEqualTo(clientToSave.fAdressStreet);
        assertThat(phones).isNotNull();
        assertThat(phones).hasSize(3);
        assertThat(phones.get(0).number).isEqualTo(clientToSave.mobilePhones.get(0));
        assertThat(phones.get(1).number).isEqualTo(clientToSave.mobilePhones.get(1));
        assertThat(phones.get(2).number).isEqualTo(clientToSave.homePhone.get(0));
    }

    @Test
    public void testRemoveClient() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();

        CharmDot charmDot = new CharmDot();
        charmDot.id = 1;
        charmDot.name = "меланхолик";
        charmDot.description = "asdasd";
        charmDot.energy = (float) 123;
        charmTestDao.get().insertCharm(charmDot);

        ClientDot clientDot = new ClientDot();
        clientDot.id = 1;
        clientDot.name = "Владимир";
        clientDot.surname = "Путин";
        clientDot.patronymic = "Владимирович";
        clientDot.gender = "MALE";
        clientDot.birth_date = new SimpleDateFormat( "yyyyMMdd" ).parse( "20100520" );
        clientDot.charm_id = 1;
        clientTestDao.get().insertClient(clientDot);

        //
        //
        String str = clientRegister.get().removeClient(String.valueOf(1));
        //
        //

        List<ClientDot> clients = clientTestDao.get().getAllClients();

        assertThat(str).isEqualTo(String.valueOf(1));
        assertThat(clients).hasSize(0);
    }

    @Test
    public void testGetEditableClientInfo() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        adressTestDao.get().clearAdresses();
        phoneTestDao.get().clearPhones();

        CharmDot charmDot = new CharmDot();
        charmDot.id = 1;
        charmDot.name = "меланхолик";
        charmDot.description = "asdasd";
        charmDot.energy = (float) 123;
        charmTestDao.get().insertCharm(charmDot);

        ClientDot clientDot = new ClientDot();
        clientDot.id = 1;
        clientDot.name = "Владимир";
        clientDot.surname = "Путин";
        clientDot.patronymic = "Владимирович";
        clientDot.gender = "MALE";
        clientDot.birth_date = new SimpleDateFormat( "yyyyMMdd" ).parse( "20100520" );
        clientDot.charm_id = 1;
        clientTestDao.get().insertClient(clientDot);

        PhoneDot phoneDot = new PhoneDot();
        phoneDot.clientID = 1;
        phoneDot.number = "87779105332";
        phoneDot.phoneType = "MOBILE";
        phoneTestDao.get().insertPhone(phoneDot);

        AdressDot adressDot = new AdressDot();
        adressDot.id = 1;
        adressDot.clientID = 1;
        adressDot.street = "Каратал";
        adressDot.house = "15";
        adressDot.flat = "25";
        adressDot.adressType = "REG";
        adressTestDao.get().insertAdress(adressDot);

        //
        //
        ClientDetails clientDetails = clientRegister.get().getEditableClientInfo("1");
        //
        //

        assertThat(clientDetails).isNotNull();
        assertThat(clientDetails.id).isEqualTo(clientDot.id);
        assertThat(clientDetails.name).isEqualTo(clientDot.name);
        assertThat(clientDetails.surname).isEqualTo(clientDot.surname);
        assertThat(clientDetails.patronymic).isEqualTo(clientDot.patronymic);
        assertThat(clientDetails.gender).isEqualTo(clientDot.gender);
        assertThat(clientDetails.charm_id).isEqualTo(charmDot.id);
        assertThat(clientDetails.mobilePhones.get(0)).isEqualTo(phoneDot.number);
        assertThat(clientDetails.rAdressStreet).isEqualTo(adressDot.street);
    }

    @Test
    public void testMiddlePageSortedByAgeUp() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);

        List<ClientDot> clientDots = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ClientDot clientDot = new ClientDot();
            clientDot.id = i;
            clientDot.name = RND.str(10);
            clientDot.surname = RND.str(10);
            clientDot.patronymic = RND.str(10);
            clientDot.birth_date = RND.dateYears(1990, 2015);
            clientDot.gender = RND.str(4);
            clientDot.charm_id = RND.plusInt(4) + 1;
            clientTestDao.get().insertClient(clientDot);
            clientDots.add(clientDot);
        }

        clientDots.sort(new Comparator<ClientDot>() {
            @Override
            public int compare(ClientDot o1, ClientDot o2) {
                if (o1.CountAge() > o2.CountAge()) {
                    return 1;
                } else
                if (o1.CountAge() < o2.CountAge()) {
                    return -1;
                } else {return 0;}
            }
        });

        FilterSortParams filterSortParams = new FilterSortParams("", "age", "up");
        ClientsListParams clientsListParams = new ClientsListParams(5, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        for (int i = 0; i < 3; i++) {
            assertThat(clients.clientInfos.get(i).id).isEqualTo(clientDots.get(i + 12).id);
            assertThat(clients.clientInfos.get(i).age).isEqualTo(clientDots.get(i + 12).CountAge());
            String fio = clientDots.get(i + 12).surname + " " + clientDots.get(i + 12).name + " " + clientDots.get(i + 12).patronymic;
            assertThat(clients.clientInfos.get(i).fio).isEqualTo(fio);
        }
    }

    //TODO: нет сортировки по убыванию
    @Test
    public void testMiddlePageSortedByAgeDown() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);

        List<ClientDot> clientDots = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ClientDot clientDot = new ClientDot();
            clientDot.id = i;
            clientDot.name = RND.str(10);
            clientDot.surname = RND.str(10);
            clientDot.patronymic = RND.str(10);
            clientDot.birth_date = RND.dateYears(1990, 2015);
            clientDot.gender = RND.str(4);
            clientDot.charm_id = RND.plusInt(4) + 1;
            clientTestDao.get().insertClient(clientDot);
            clientDots.add(clientDot);
        }

        clientDots.sort(new Comparator<ClientDot>() {
            @Override
            public int compare(ClientDot o1, ClientDot o2) {
                if (o1.CountAge() < o2.CountAge()) {
                    return 1;
                } else
                if (o1.CountAge() > o2.CountAge()) {
                    return -1;
                } else {return 0;}
            }
        });

        FilterSortParams filterSortParams = new FilterSortParams("", "age", "down");
        ClientsListParams clientsListParams = new ClientsListParams(5, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        for (int i = 0; i < 3; i++) {
            assertThat(clients.clientInfos.get(i).id).isEqualTo(clientDots.get(i + 12).id);
            assertThat(clients.clientInfos.get(i).age).isEqualTo(clientDots.get(i + 12).CountAge());
            String fio = clientDots.get(i + 12).surname + " " + clientDots.get(i + 12).name + " " + clientDots.get(i + 12).patronymic;
            assertThat(clients.clientInfos.get(i).fio).isEqualTo(fio);
        }
    }
    @Test
    public void testLastPageSortedByAgeUp() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);

        List<ClientDot> clientDots = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ClientDot clientDot = new ClientDot();
            clientDot.id = i;
            clientDot.name = RND.str(10);
            clientDot.surname = RND.str(10);
            clientDot.patronymic = RND.str(10);
            clientDot.birth_date = RND.dateYears(1990, 2015);
            clientDot.gender = RND.str(4);
            clientDot.charm_id = RND.plusInt(4) + 1;
            clientTestDao.get().insertClient(clientDot);
            clientDots.add(clientDot);
        }

        clientDots.sort(new Comparator<ClientDot>() {
            @Override
            public int compare(ClientDot o1, ClientDot o2) {
                if (o1.CountAge() > o2.CountAge()) {
                    return 1;
                } else
                if (o1.CountAge() < o2.CountAge()) {
                    return -1;
                } else {return 0;}
            }
        });

        FilterSortParams filterSortParams = new FilterSortParams("", "age", "up");
        ClientsListParams clientsListParams = new ClientsListParams(10, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        for (int i = 0; i < 3; i++) {
            assertThat(clients.clientInfos.get(i).id).isEqualTo(clientDots.get(i + 27).id);
            assertThat(clients.clientInfos.get(i).age).isEqualTo(clientDots.get(i + 27).CountAge());
            String fio = clientDots.get(i + 27).surname + " " + clientDots.get(i + 27).name + " " + clientDots.get(i + 27).patronymic;
            assertThat(clients.clientInfos.get(i).fio).isEqualTo(fio);
        }
    }
    @Test
    public void testLastPageSortedByAgeDown() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);

        List<ClientDot> clientDots = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ClientDot clientDot = new ClientDot();
            clientDot.id = i;
            clientDot.name = RND.str(10);
            clientDot.surname = RND.str(10);
            clientDot.patronymic = RND.str(10);
            clientDot.birth_date = RND.dateYears(1990, 2015);
            clientDot.gender = RND.str(4);
            clientDot.charm_id = RND.plusInt(4) + 1;
            clientTestDao.get().insertClient(clientDot);
            clientDots.add(clientDot);
        }

        clientDots.sort(new Comparator<ClientDot>() {
            @Override
            public int compare(ClientDot o1, ClientDot o2) {
                if (o1.CountAge() < o2.CountAge()) {
                    return 1;
                } else
                if (o1.CountAge() > o2.CountAge()) {
                    return -1;
                } else {return 0;}
            }
        });

        FilterSortParams filterSortParams = new FilterSortParams("", "age", "down");
        ClientsListParams clientsListParams = new ClientsListParams(10, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        for (int i = 0; i < 3; i++) {
            assertThat(clients.clientInfos.get(i).id).isEqualTo(clientDots.get(i + 27).id);
            assertThat(clients.clientInfos.get(i).age).isEqualTo(clientDots.get(i + 27).CountAge());
            String fio = clientDots.get(i + 27).surname + " " + clientDots.get(i + 27).name + " " + clientDots.get(i + 27).patronymic;
            assertThat(clients.clientInfos.get(i).fio).isEqualTo(fio);
        }
    }

    @Test
    public void testGetFilteredClientsInfo() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("Ал", "", "");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(1);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(1);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(0).age).isEqualTo(20);
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).minCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).maxCash).isEqualTo(25000);

        assertThat(view.rowList).hasSize(1);
        assertThat(view.rowList.get(0).fio).isEqualTo("Пушкин Александр Сергеевич");
    }
    @Test
    public void testGetFilteredClientsInfoSortedByFIOUp() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "fio", "up");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).totalCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).totalCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).minCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).minCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).minCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).maxCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).maxCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).maxCash).isEqualTo(25000);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(3).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(view.rowList.get(1).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(view.rowList.get(0).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(view.rowList.get(2).fio).isEqualTo("Пушкин Александр Сергеевич");
    }
    @Test
    public void testGetFilteredClientsInfoSortedByFIODown() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "fio", "down");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).totalCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(2).totalCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(0).minCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).minCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(2).minCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(0).maxCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).maxCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(2).maxCash).isEqualTo(43200);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(0).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(view.rowList.get(2).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(view.rowList.get(3).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(view.rowList.get(1).fio).isEqualTo("Пушкин Александр Сергеевич");
    }
    @Test
    public void testGetFilteredClientsInfoSortedByAgeUp() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "age", "up");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(clients.clientInfos.get(0).age).isEqualTo(20);
        assertThat(clients.clientInfos.get(1).age).isEqualTo(20);
        assertThat(clients.clientInfos.get(2).age).isEqualTo(28);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(0).age).isEqualTo(20);
        assertThat(view.rowList.get(1).age).isEqualTo(20);
        assertThat(view.rowList.get(2).age).isEqualTo(28);
        assertThat(view.rowList.get(3).age).isEqualTo(120);
    }
    @Test
    public void testGetFilteredClientsInfoSortedByAgeDown() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "age", "down");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(0).age).isEqualTo(120);
        assertThat(clients.clientInfos.get(1).age).isEqualTo(28);
        assertThat(clients.clientInfos.get(2).age).isEqualTo(20);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(0).age).isEqualTo(120);
        assertThat(view.rowList.get(1).age).isEqualTo(28);
        assertThat(view.rowList.get(2).age).isEqualTo(20);
        assertThat(view.rowList.get(3).age).isEqualTo(20);
    }
    @Test
    public void testGetFilteredClientsInfoSortedByCashUp() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "totalCash", "up");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).totalCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(2).totalCash).isEqualTo(25000);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(0).totalCash).isEqualTo(0);
        assertThat(view.rowList.get(1).totalCash).isEqualTo(0);
        assertThat(view.rowList.get(2).totalCash).isEqualTo(25000);
        assertThat(view.rowList.get(3).totalCash).isEqualTo(43200);
    }
    @Test
    public void testGetFilteredClientsInfoSortedByCashDown() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "totalCash", "down");
        ClientsListParams clientsListParams = new ClientsListParams(1, filterSortParams);
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Пушкин", view, filterSortParams);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo(clientsListParams);
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(clients.clientInfos.get(1).fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clients.clientInfos.get(2).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(1).totalCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(2).totalCash).isEqualTo(0);

        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(3).totalCash).isEqualTo(0);
        assertThat(view.rowList.get(2).totalCash).isEqualTo(0);
        assertThat(view.rowList.get(1).totalCash).isEqualTo(25000);
        assertThat(view.rowList.get(0).totalCash).isEqualTo(43200);
    }

    @Test
    public void testGetCharms() throws Exception {
        charmTestDao.get().clearCharms();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        //
        //
        List<Charm> charms = clientRegister.get().getCharms();
        //
        //

        for(Charm charm : charms) {
            System.out.println(charm.name);
        }

        assertThat(charms).isNotNull();
        assertThat(charms).hasSize(4);

        assertThat(charms.get(0)).isNotNull();
        assertThat(charms.get(0).name).isEqualTo("холерик");
        assertThat(charms.get(1)).isNotNull();
        assertThat(charms.get(1).name).isEqualTo("меланхолик");
        assertThat(charms.get(2)).isNotNull();
        assertThat(charms.get(2).name).isEqualTo("флегматик");
        assertThat(charms.get(3)).isNotNull();
        assertThat(charms.get(3).name).isEqualTo("сангвиник");
    }

    @Test
    public void testGetReportParams() {
        ReportParamsToSave reportParamsToSave = new ReportParamsToSave(1, "PDF", "Sanzhar", "","","");

        //
        //
        clientRegister.get().saveReportParams(reportParamsToSave);
        ReportParamsToSave reportParamsToSave1 = clientRegister.get().popReportParams(1);
        //
        //

        assertThat(reportParamsToSave1.report_id).isEqualTo(1);
        assertThat(reportParamsToSave1.report_type).isNotNull();
        assertThat(reportParamsToSave1.username).isNotNull();
        assertThat(reportParamsToSave1.filterStr).isNotNull();
        assertThat(reportParamsToSave1.sortBy).isNotNull();
        assertThat(reportParamsToSave1.sortOrder).isNotNull();
    }

    private static class TestView implements ClientsListReportView {

        public String title;
        public String userName;

        @Override
        public void start(String title) {
            this.title = title;
        }

        List<ClientListRow> rowList = new ArrayList<ClientListRow>();

        @Override
        public void append(ClientListRow clientListRow) {
            rowList.add(clientListRow);
        }

        @Override
        public void finish(String userName) {
            this.userName = userName;
        }
    }
    @Test
    public void genClientListReport() throws Exception {
        clientTestDao.get().clearClients();
        charmTestDao.get().clearCharms();
        accountTestDao.get().clearAccounts();

        standDb.get().charmStorage.values().stream()
                .forEach(charmTestDao.get()::insertCharm);
        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        TestView view = new TestView();
        FilterSortParams filterSortParams = new FilterSortParams("", "", "");
        ClientsListReportParams clientsListReportParams = new ClientsListReportParams("Pushkin", view, filterSortParams);
        //
        //
        clientRegister.get().genClientListReport(clientsListReportParams);
        //
        //

        view.rowList.sort(new Comparator<ClientListRow>() {
            @Override
            public int compare(ClientListRow o1, ClientListRow o2) {
                return (o1.fio.compareTo(o2.fio));
            }
        });
        assertThat(view.rowList).hasSize(4);
        assertThat(view.rowList.get(3).fio).isEqualTo("Толстой Лев Николаевич");
        assertThat(view.rowList.get(1).fio).isEqualTo("Лермонтов Михаил Юрьевич");
        assertThat(view.rowList.get(0).fio).isEqualTo("Бурумбай Санжар Ришадулы");
        assertThat(view.rowList.get(2).fio).isEqualTo("Пушкин Александр Сергеевич");
    }
}
