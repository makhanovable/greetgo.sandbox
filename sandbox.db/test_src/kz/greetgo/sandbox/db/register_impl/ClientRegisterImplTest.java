package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<ClientTestDao> clientTestDao;

    @Test
    public void add_new_client() {
        ClientDetails randomClientDetail = createRandomClientDetail(-1);
        ClientRecord expectedClientRecord = createClientRecordFromClientDetail(randomClientDetail, true);

        //
        //
        //
        ClientRecord clientRecord = clientRegister.get().addNewClient(randomClientDetail);
        //
        //
        //

        assertThat(clientRecord).isNotNull();
        assertThat(clientRecord.id).isNotNull();
        assertThat(clientRecord.name).isEqualTo(expectedClientRecord.name);
        assertThat(clientRecord.age).isEqualTo(expectedClientRecord.age);
        assertThat(clientRecord.charm).isEqualTo(expectedClientRecord.charm);
        assertThat(clientRecord.total).isEqualTo(0);
        assertThat(clientRecord.max).isEqualTo(0);
        assertThat(clientRecord.min).isEqualTo(0);
    }

    @Test
    public void edit_client() {
        ClientDetails randomClientDetail = clientTestDao.get().getRandomClientDetail();
        ClientDetails editedClientDetail = createRandomClientDetail(randomClientDetail.id);
        ClientRecord expectedClientRecord = createClientRecordFromClientDetail(editedClientDetail, false);

        //
        //
        //
        ClientRecord clientRecord = clientRegister.get().editClient(randomClientDetail);
        //
        //
        //

        assertThat(clientRecord).isNotNull();
        assertThat(clientRecord.id).isNotNull();
        assertThat(clientRecord.name).isEqualTo(expectedClientRecord.name);
        assertThat(clientRecord.age).isEqualTo(expectedClientRecord.age);
        assertThat(clientRecord.charm).isEqualTo(expectedClientRecord.charm);
        assertThat(clientRecord.total).isEqualTo(0);
        assertThat(clientRecord.max).isEqualTo(0);
        assertThat(clientRecord.min).isEqualTo(0);
    }

    @Test
    public void delete_client() {
        int deletedId = RND.plusInt(100); // TODO get valid id

        //
        //
        //
        clientRegister.get().deleteClient(deletedId);
        Client client = clientTestDao.get().getClientById(deletedId);
        //
        //
        //

        assertThat(client).isNull();
    }

    @Test
    public void get_charms() {
        int expected_size = clientTestDao.get().getCharmsCount();

        //
        //
        //
        List<Charm> charms = clientRegister.get().getCharms();
        //
        //
        //

        assertThat(charms).isNotNull();
        assertThat(charms.size()).isEqualTo(expected_size); // TODO check for null names, descrip, ids
    }

    @Test
    public void filter_by_name() {
        Options options = new Options();
        options.filter = RND.str(10); // TODO add to DB items that matches to filter

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> filteredList = clientRecordInfo.items;
        //
        //
        //


        assertThat(clientRecordInfo).isNotNull();
        assertThat(filteredList).isNotNull();
        for (ClientRecord aFilteredList : filteredList) // TODO как то не красиво
            assertThat(aFilteredList.name).contains(options.filter);
    }

    @Test
    public void sort_by_name() {
        Options options = new Options();
        options.sort = "name";
        options.order = "asc"; // TODO random

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> sortedList = clientRecordInfo.items;
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(sortedList).isNotNull();
        // TODO check is sorted
    }

    @Test
    public void sort_by_age() {
        Options options = new Options();
        options.sort = "age";
        options.order = "desc"; // TODO random

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> sortedList = clientRecordInfo.items;
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(sortedList).isNotNull();
        // TODO check is sorted
    }

    @Test
    public void sort_by_total_balance() {
        Options options = new Options();
        options.sort = "total";
        options.order = "asc"; // TODO random

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> sortedList = clientRecordInfo.items;
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(sortedList).isNotNull();
        // TODO check is sorted
    }

    @Test
    public void sort_by_max_balance() {
        Options options = new Options();
        options.sort = "max";
        options.order = "asc"; // TODO random

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> sortedList = clientRecordInfo.items;
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(sortedList).isNotNull();
        // TODO check is sorted
    }

    @Test
    public void sort_by_min_balance() {
        Options options = new Options();
        options.sort = "min";
        options.order = "desc"; // TODO random

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        List<ClientRecord> sortedList = clientRecordInfo.items;
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(sortedList).isNotNull();
        // TODO check is sorted
    }

    @Test
    public void get_client_by_id() {
        int id = RND.plusInt(100); // TODO get valid id
        ClientDetails expectedClientDetails = clientTestDao.get().getClientDetailsById(id);

        //
        //
        //
        ClientDetails clientDetails = clientRegister.get().getClientById(id);
        //
        //
        //

        assertThat(clientDetails).isNotNull();
        assertThat(clientDetails).isEqualsToByComparingFields(expectedClientDetails);
    }

    @Test
    public void pagination_of_filtered_list_by_name() {
        Options options = new Options();
        options.filter = RND.str(10); // TODO add to DB items that matches to filter
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    @Test
    public void pagination_of_sorted_list_by_name() {
        Options options = new Options();
        options.sort = "name";
        options.order = "asc"; // TODO random
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    @Test
    public void pagination_of_sorted_list_by_age() {
        Options options = new Options();
        options.sort = "age";
        options.order = "asc"; // TODO random
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    @Test
    public void pagination_of_sorted_list_by_total_balance() {
        Options options = new Options();
        options.sort = "total";
        options.order = "asc"; // TODO random
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    @Test
    public void pagination_of_sorted_list_by_min_balance() {
        Options options = new Options();
        options.sort = "min";
        options.order = "asc"; // TODO random
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    @Test
    public void pagination_of_sorted_list_by_max_balance() {
        Options options = new Options();
        options.sort = "max";
        options.order = "asc"; // TODO random
        options.size = RND.intStr(100);
        options.page = RND.intStr(100);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.total_count).isNotNull();
        // TODO подумать
    }

    private ClientDetails createRandomClientDetail(int id) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.name = RND.str(10);
        clientDetails.surname = RND.str(10);
        clientDetails.patronymic = RND.str(10);
        clientDetails.gender = Gender.FEMALE.name(); // TODO edit
        clientDetails.birth_date = new Date().toString(); // TODO edit use RND.dateYear etc

        Integer charms_count = clientTestDao.get().getCharmsCount();
        if (charms_count == null || charms_count == 0)
            insertRandomCharm();
        charms_count = clientTestDao.get().getCharmsCount();

        clientDetails.charm = RND.plusInt(charms_count + 1);
        clientDetails.addrRegStreet = RND.str(10);
        clientDetails.addrRegHome = RND.str(10);
        clientDetails.addrRegFlat = RND.str(10);
        String[] phones = new String[5];
        for (int i = 0; i < 5; i++){
            phones[i] = RND.str(10);
        }
        clientDetails.phones = phones;
        if (id != -1)
            clientDetails.id = id;
        return clientDetails;
    }

    private ClientRecord createClientRecordFromClientDetail(ClientDetails clientDetails, boolean isNew) {
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id = clientDetails.id;
        clientRecord.name = clientDetails.surname + " " + clientDetails.name + " " + clientDetails.patronymic; // TODO add patronymic
        clientRecord.charm = clientTestDao.get().getCharmById(clientDetails.charm);
        clientRecord.age = calculateAge(clientDetails.birth_date);
        if (!isNew) {
            clientRecord.total = clientTestDao.get().getTotalBalanceById(clientDetails.id);
            clientRecord.max = clientTestDao.get().getMaxBalanceById(clientDetails.id);
            clientRecord.min = clientTestDao.get().getMinBalanceById(clientDetails.id);
        }
        return clientRecord;
    }

    private int calculateAge(String birth_date) {
        return 0; // TODO edit
    }

    private void fill_tables_with_random_values() {
        clientTestDao.get().TRUNCATE();
        int count = 100;
        for (int i = 0; i < count; i++) {

            // filling client
            Client client = new Client();
            client.id = i;
            client.surname = RND.str(10);
            client.name = RND.str(10);
            client.patronymic = RND.str(10);
            client.gender = Gender.FEMALE; // TODO random
            client.birth_date = new Date(); // TODO set random date
            client.charm = RND.plusInt(count);
            clientTestDao.get().insert_random_client(client);

            // filling client_addr
            ClientAddr clientAddr = new ClientAddr();
            clientAddr.client = i;
            clientAddr.type = ClientAddrType.REG;
            clientAddr.street = RND.str(10);
            clientAddr.house = RND.str(10);
            clientAddr.flat = RND.str(10);
            clientTestDao.get().insert_random_client_addr(clientAddr);

            // filling client_phone
            ClientPhone clientPhone = new ClientPhone();
            clientPhone.client = i;
            clientPhone.number = RND.str(10);
            clientPhone.type = PhoneType.WORK;
            clientTestDao.get().insert_random_client_phone(clientPhone);

            // filling client_account
            ClientAccount clientAccount = new ClientAccount();
            clientAccount.client = i;
            clientAccount.id = i;
            clientAccount.money = RND.plusInt(1000) * 1.1f;
            clientAccount.number = RND.str(10);
            clientAccount.registered_at = null; // TODO edit
            clientTestDao.get().insert_random_client_account(clientAccount);

            // filling client_account_transaction
            ClientAccountTransaction clientAccountTransaction = new ClientAccountTransaction();
            clientAccountTransaction.account = i;
            clientAccountTransaction.id = i;
            clientAccountTransaction.money = RND.plusInt(1000) * 1.1f;
            clientAccountTransaction.finished_at = null; // TODO edit
            clientAccountTransaction.type = i;
            clientTestDao.get().insert_random_client_account_transaction(clientAccountTransaction);

            // filling transaction_type
            TransactionType transactionType = new TransactionType();
            transactionType.id = i;
            transactionType.code = RND.str(10);
            transactionType.name = RND.str(10);
            clientTestDao.get().insert_random_transaction_type(transactionType);
        }
    }

    private void insertRandomCharm() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            Charm charm = new Charm();
            charm.id = i;
            charm.name = RND.str(10);
            charm.description = RND.str(10);
            charm.energy = RND.plusInt(1000) * 1.1f;
            clientTestDao.get().insert_random_charm(charm);
        }
    }

}
