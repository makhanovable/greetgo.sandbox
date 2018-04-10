package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AdressDot;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.*;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<CharmTestDao> charmTestDao;
    public BeanGetter<ClientTestDao> clientTestDao;
    public BeanGetter<AccountTestDao> accountTestDao;
    public BeanGetter<PhoneTestDao> phoneTestDao;
    public BeanGetter<AdressTestDao> adressTestDao;
    public BeanGetter<StandDb> standDb;

    @Test
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
        List<Adress> adresses = adressTestDao.get().getAdress(1);
        List<Phone> phones = phoneTestDao.get().getPhones(1);
        //
        //

        assertThat(clientRecord).isNotNull();
        assertThat(clientRecord.id).isEqualTo(1);
        assertThat(clientRecord.fio).isEqualTo("Путин Владимир Владимирович");
        assertThat(clientRecord.charm).isEqualTo("меланхолик");
        assertThat(clientRecord.age).isEqualTo(20);
        assertThat(adresses).isNotNull();
        assertThat(adresses).hasSize(1);
        assertThat(adresses.get(0).clientID).isEqualTo(1);
        assertThat(adresses.get(0).adressType).isEqualTo("REG");
        assertThat(adresses.get(0).street).isEqualTo("Каратал");
        assertThat(phones).isNotNull();
        assertThat(phones).hasSize(1);
        assertThat(phones.get(0).clientID).isEqualTo(1);
        assertThat(phones.get(0).number).isEqualTo("87779105332");
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
        List<Adress> adresses = adressTestDao.get().getAdress(clientRecord1.id);
        List<Phone> phones = phoneTestDao.get().getPhones(clientRecord1.id);
        //
        //

        assertThat(clientRecord2).isNotNull();
        assertThat(clientRecord2.id).isEqualTo(clientRecord1.id);
        assertThat(clientRecord2.fio).isEqualTo("Пушкин Александр Сергеевич");
        assertThat(clientRecord2.charm).isEqualTo("меланхолик");
        assertThat(clientRecord2.age).isEqualTo(30);
        assertThat(adresses).isNotNull();
        assertThat(adresses).hasSize(2);
        assertThat(adresses.get(0).adressType).isEqualTo("REG");
        assertThat(adresses.get(0).street).isEqualTo("Каратал");
        assertThat(adresses.get(1).adressType).isEqualTo("FACT");
        assertThat(adresses.get(1).street).isEqualTo("Кабанбай батыра");
        assertThat(phones).isNotNull();
        assertThat(phones).hasSize(3);
        assertThat(phones.get(0).number).isEqualTo("87779105332");
        assertThat(phones.get(1).number).isEqualTo("87474415332");
        assertThat(phones.get(2).number).isEqualTo("87282305227");
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
        List<ClientDot> clients = clientTestDao.get().getAllClients();
        //
        //

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
        assertThat(clientDetails.charm_id).isEqualTo(charmDot.id);
        assertThat(clientDetails.mobilePhones.get(0)).isEqualTo(phoneDot.number);
        assertThat(clientDetails.rAdressStreet).isEqualTo(adressDot.street);
    }

    @Test
    public void testGetFilteredClientsInfo() throws Exception {
        clientTestDao.get().clearClients();

        standDb.get().clientStorage.values().stream()
                .forEach(clientTestDao.get()::insertClient);
        standDb.get().accountStorage.values().stream()
                .forEach(accountTestDao.get()::insertAccount);

        //
        //
        ClientToReturn clients = clientRegister.get().getFilteredClientsInfo("1", "","fio","up");
        //
        //

        assertThat(clients).isNotNull();
        assertThat(clients.pageCount).isEqualTo(2);
        assertThat(clients.clientInfos).isNotNull();
        assertThat(clients.clientInfos).hasSize(3);
        assertThat(clients.clientInfos.get(0).id).isEqualTo(3);
        assertThat(clients.clientInfos.get(1).id).isEqualTo(2);
        assertThat(clients.clientInfos.get(2).id).isEqualTo(1);
        assertThat(clients.clientInfos.get(0).totalCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).totalCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).totalCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).minCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).minCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).minCash).isEqualTo(25000);
        assertThat(clients.clientInfos.get(0).maxCash).isEqualTo(0);
        assertThat(clients.clientInfos.get(1).maxCash).isEqualTo(43200);
        assertThat(clients.clientInfos.get(2).maxCash).isEqualTo(25000);
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

}