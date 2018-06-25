package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.*;

import static kz.greetgo.sandbox.db.test.util.RandomDataUtil.*;
import static kz.greetgo.sandbox.db.util.ClientHelperUtil.calculateAge;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<ClientTestDao> clientTestDao;


    @Test
    public void add_new_client() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
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
        assertThat(clientRecord.total).isEqualTo(0f);
        assertThat(clientRecord.min).isEqualTo(0f);
        assertThat(clientRecord.max).isEqualTo(0f);
    }

    private ClientDetails randomClientD = null; // used in edit_client and get_client_by_id @Test

    @Test
    public void edit_client() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(101, true, null);
        ClientDetails editedClientDetail = createRandomClientDetail(randomClientD.id);
        ClientRecord expectedClientRecord = createClientRecordFromClientDetail(editedClientDetail, false);

        //
        //
        //
        ClientRecord clientRecord = clientRegister.get().editClient(editedClientDetail);
        //
        //
        //

        assertThat(clientRecord).isNotNull();
        assertThat(clientRecord.id).isEqualTo(expectedClientRecord.id);
        assertThat(clientRecord.name).isEqualTo(expectedClientRecord.name);
        assertThat(clientRecord.age).isEqualTo(expectedClientRecord.age);
        assertThat(clientRecord.charm).isEqualTo(expectedClientRecord.charm);
        assertThat(clientRecord.total).isEqualTo(expectedClientRecord.total);
        assertThat(clientRecord.min).isEqualTo(expectedClientRecord.min);
        assertThat(clientRecord.max).isEqualTo(expectedClientRecord.max);
    }

    @Test
    public void delete_client() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        List<Integer> ids = clientTestDao.get().getAllActualClientIds();
        int deletedId = ids.get(RND.plusInt(ids.size()));

        //
        //
        //
        clientRegister.get().deleteClient(deletedId);
        //
        //
        //


        Client client = clientTestDao.get().getClientById(deletedId);
        List<ClientAddr> clientAddr = clientTestDao.get().getClientAddrsById(deletedId);
        List<ClientPhone> clientPhone = clientTestDao.get().getClientPhonesById(deletedId);
        List<ClientAccount> clientAccount = clientTestDao.get().getClientAccountsById(deletedId);

        assertThat(client).isNull();
        assertThat(clientAddr.size()).isEqualTo(0);
        assertThat(clientPhone.size()).isEqualTo(0);
        assertThat(clientAccount.size()).isEqualTo(0);
    }

    @Test
    public void get_charms() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Integer expected_size = clientTestDao.get().getCharmsCount();

        //
        //
        //
        List<Charm> charms = clientRegister.get().getCharms();
        //
        //
        //

        assertThat(charms).isNotNull();
        assertThat(charms.size()).isEqualTo(expected_size);
    }

    @Test
    public void filter_by_name() {
        Options options = new Options();
        options.filter = generateRndStr(10);
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, options.filter);

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //


        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        for (ClientRecord aFilteredList : clientRecordInfo.items)
            assertThat(aFilteredList.name).contains(options.filter);
    }

    @Test
    public void sort_by_name_asc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "name";
        options.order = "asc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
    }

    @Test
    public void sort_by_name_desc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "name";
        options.order = "desc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();

        Collections.reverse(clientRecordInfo.items);
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));

    }

    @Test
    public void sort_by_age_asc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "age";
        options.order = "asc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.age));

    }

    @Test
    public void sort_by_age_desc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "age";
        options.order = "desc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        Collections.reverse(clientRecordInfo.items);
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.age));

    }

    @Test
    public void sort_by_total_balance_asc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "total";
        options.order = "asc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.total));
    }

    @Test
    public void sort_by_total_balance_desc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "total";
        options.order = "desc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        Collections.reverse(clientRecordInfo.items);
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.total));
    }

    @Test
    public void sort_by_max_balance_asc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "max";
        options.order = "asc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.max));
    }

    @Test
    public void sort_by_max_balance_desc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "max";
        options.order = "desc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        Collections.reverse(clientRecordInfo.items);
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.max));
    }

    @Test
    public void sort_by_min_balance_asc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "min";
        options.order = "asc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.min));
    }

    @Test
    public void sort_by_min_balance_desc() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "min";
        options.order = "desc";

        //
        //
        //
        ClientRecordInfo clientRecordInfo = clientRegister.get().getClientRecords(options);
        //
        //
        //

        assertThat(clientRecordInfo).isNotNull();
        assertThat(clientRecordInfo.items).isNotNull();
        Collections.reverse(clientRecordInfo.items);
        assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.min));
    }

    @Test
    public void get_client_by_id() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(101, true, null);
        ClientDetails expectedClientDetails = randomClientD;
        int id = expectedClientDetails.id;

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
    public void pagination_of_list() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
    }

    @Test
    public void pagination_of_filtered_list_by_name() {
        Options options = new Options();
        options.filter = generateRndStr(10);
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, options.filter);

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        for (ClientRecord aFilteredList : clientRecordInfo.items)
            assertThat(aFilteredList.name).contains(options.filter);
    }

    @Test
    public void pagination_of_sorted_list_by_name() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(1000, false, null);
        Options options = new Options();
        options.sort = "name";
        options.order = randomStr("asc", "desc");
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        if (options.order.equals("asc"))
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        else {
            Collections.reverse(clientRecordInfo.items);
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        }
    }

    @Test
    public void pagination_of_sorted_list_by_age() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "age";
        options.order = randomStr("asc", "desc");
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        if (options.order.equals("asc"))
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        else {
            Collections.reverse(clientRecordInfo.items);
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        }
    }

    @Test
    public void pagination_of_sorted_list_by_total_balance() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "total";
        options.order = randomStr("asc", "desc");
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        if (options.order.equals("asc"))
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        else {
            Collections.reverse(clientRecordInfo.items);
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        }
    }

    @Test
    public void pagination_of_sorted_list_by_min_balance() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "min";
        options.order = randomStr("asc", "desc");
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        if (options.order.equals("asc"))
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        else {
            Collections.reverse(clientRecordInfo.items);
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        }
    }

    @Test
    public void pagination_of_sorted_list_by_max_balance() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.sort = "max";
        options.order = randomStr("asc", "desc");
        options.size = Integer.toString(RND.plusInt(10));
        options.page = Integer.toString(RND.plusInt(10));

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
        assertThat(clientRecordInfo.items.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        if (options.order.equals("asc"))
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        else {
            Collections.reverse(clientRecordInfo.items);
            assertThat(clientRecordInfo.items).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        }
    }

    private ClientDetails createRandomClientDetail(int id) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.name = generateRndStr(10);
        clientDetails.surname = generateRndStr(10);
        clientDetails.patronymic = generateRndStr(10);
        clientDetails.gender = randomize(Gender.class).name();
        clientDetails.birth_date = randomDate().toString();

        Integer charms_count = clientTestDao.get().getCharmsCount();
        if (charms_count == null)
            charms_count = -2;

        clientDetails.charm = RND.plusInt(charms_count + 1);
        clientDetails.addrRegStreet = RND.str(10);
        clientDetails.addrRegHome = RND.str(10);
        clientDetails.addrRegFlat = RND.str(10); // TODO add FactAddr
        ClientPhone[] phones = new ClientPhone[5];
        for (int i = 0; i < 5; i++) {
            phones[i] = new ClientPhone();
            phones[i].type = randomize(PhoneType.class);
            phones[i].number = RND.str(10);
            if (id != -1)
                phones[i].client = id;
        }
        clientDetails.phones = phones;
        if (id != -1)
            clientDetails.id = id;
        return clientDetails;
    }

    private ClientRecord createClientRecordFromClientDetail(ClientDetails clientDetails, boolean isNew) {
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.name = clientDetails.surname + " " + clientDetails.name;
        if (clientDetails.patronymic != null)
            clientRecord.name += " " + clientDetails.patronymic;
        clientRecord.charm = clientTestDao.get().getCharmById(clientDetails.charm);
        clientRecord.age = calculateAge(clientDetails.birth_date);
        if (!isNew) {
            List<Float> list = clientTestDao.get().getClientAccountsMoneyById(clientDetails.id);
            Collections.sort(list);
            clientRecord.total = 0f;
            for (float i : list)
                clientRecord.total += i;
            clientRecord.min = list.isEmpty() ? 0f : list.get(0);
            clientRecord.max = list.isEmpty() ? 0f : list.get(list.size() - 1);
            clientRecord.id = clientDetails.id;
        } else {
            clientRecord.total = 0f;
            clientRecord.min = 0f;
            clientRecord.max = 0f;
        }
        return clientRecord;
    }

    private void remove_all_data_from_tables() {
        clientTestDao.get().TRUNCATE();
    }

    private void fill_tables_with_random_values(int count, boolean isNeedGenerateClientDetail, String nameFilter) {
        int pick = RND.plusInt(count);
        for (int i = 0; i < count; i++) {
            // filling client
            Client client = new Client();
            if (nameFilter != null && i % 3 == 0) {
                client.name = generateRndStr(2) + nameFilter + generateRndStr(2);
            } else
                client.name = generateRndStr(10);
            client.surname = generateRndStr(10);
            if (i < 20)
                client.patronymic = generateRndStr(10);
            client.gender = randomize(Gender.class);
            client.birth_date = randomDate();
            client.charm = RND.plusInt(count); // TODO
            Integer id = clientTestDao.get().insert_random_client(client);
            client.id = id;

            // filling client_addr
            ClientAddr clientAddr = new ClientAddr();
            clientAddr.client = id;
            clientAddr.type = randomize(ClientAddrType.class);
            clientAddr.street = RND.str(10);
            clientAddr.house = RND.str(10);
            clientAddr.flat = RND.str(10);
            clientTestDao.get().insert_random_client_addr(clientAddr);

            // filling client_phone
            ClientPhone clientPhone = new ClientPhone();
            clientPhone.client = id;
            clientPhone.number = RND.intStr(10);
            clientPhone.type = randomize(PhoneType.class);
            clientTestDao.get().insert_random_client_phone(clientPhone);

            // filling client_account
            if (i < 10) {
                for (int j = 0; j < 2; j++) {
                    ClientAccount clientAccount = new ClientAccount();
                    clientAccount.client = id;
                    clientAccount.money = 0f;
                    clientAccount.number = RND.str(10);
                    Integer accId = clientTestDao.get().insert_random_client_account(clientAccount);

                    // filling client_account_transaction
                    for (int k = 0; k < 4; k++) {
                        ClientAccountTransaction clientAccountTransaction = new ClientAccountTransaction();
                        clientAccountTransaction.account = accId;
                        clientAccountTransaction.money = RND.plusInt(2000) * 1.1f - 1000f;
                        clientAccountTransaction.type = i;
                        clientTestDao.get().insert_random_client_account_transaction(clientAccountTransaction);
                    }
                }
            }

            // filling transaction_type
            TransactionType transactionType = new TransactionType();
            transactionType.id = i;
            transactionType.code = RND.str(10);
            transactionType.name = RND.str(10);
            clientTestDao.get().insert_random_transaction_type(transactionType);

            // filling charm
            Charm charm = new Charm();
            charm.id = i;
            charm.name = RND.str(10);
            charm.description = RND.str(10);
            charm.energy = RND.plusInt(1000) * 1.1f;
            clientTestDao.get().insert_random_charm(charm);

            if (isNeedGenerateClientDetail && pick == i)
                generateExistingClientDetail(client, clientAddr, clientPhone);
        }
    }

    private void generateExistingClientDetail(Client client, ClientAddr clientAddr, ClientPhone clientPhone) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.id = client.id;
        clientDetails.name = client.name;
        clientDetails.surname = client.surname;
        clientDetails.patronymic = client.patronymic;
        clientDetails.gender = client.gender.name();
        clientDetails.birth_date = client.birth_date.toString();
        clientDetails.charm = client.charm;

        if (clientAddr.type == ClientAddrType.REG) {
            clientDetails.addrRegStreet = clientAddr.street;
            clientDetails.addrRegHome = clientAddr.house;
            clientDetails.addrRegFlat = clientAddr.flat;
        } else {
            clientDetails.addrFactStreet = clientAddr.street;
            clientDetails.addrFactHome = clientAddr.house;
            clientDetails.addrFactFlat = clientAddr.flat;
        }
        clientDetails.phones = new ClientPhone[]{clientPhone};
        this.randomClientD = clientDetails;
    }

    private static class TestClientRecordsReportView implements ClientRecordsReportView {

        public String user;
        public Date created_at;
        public String link_to_download;
        public final List<ClientRecord> rowList = Lists.newArrayList();

        @Override
        public void start() {

        }

        @Override
        public void append(ClientRecord row) {
            rowList.add(row);
        }

        @Override
        public void finish(String user, Date created_at, String link_to_download) {
            this.user = user;
            this.created_at = created_at;
            this.link_to_download = link_to_download;
        }

    }


    @Test
    public void render_client_list() {
        remove_all_data_from_tables();
        fill_tables_with_random_values(100, false, null);
        Options options = new Options();
        options.size = Integer.toString(RND.plusInt(20));
        options.page = Integer.toString(0);
        TestClientRecordsReportView view = new TestClientRecordsReportView();
        String username = "someuser";
        String link = "http://link.com";


        //
        //
        //
        clientRegister.get().renderClientList(options, view, username, link);
        //
        //
        //

        assertThat(view.link_to_download).isEqualTo(link);
        assertThat(view.user).isEqualTo(username);
        assertThat(view.rowList.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
    }

}
