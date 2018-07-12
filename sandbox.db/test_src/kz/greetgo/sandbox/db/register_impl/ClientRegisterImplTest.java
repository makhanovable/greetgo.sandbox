package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.apache.ibatis.exceptions.PersistenceException;
import org.postgresql.util.PSQLException;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.stream.Collectors;

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
    public void getClientList_and_renderClientList_SortByNameAsc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.name;
        options.order = "asc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        expectedClients.sort(Comparator.comparing(o -> o.surname.toLowerCase() + o.name.toLowerCase() + o.patronymic.toLowerCase()));
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedClients.get(i).id);

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        for (int i = 0; i < expectedView.rowList.size(); i++)
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedClients.get(i).id);

    }

    @Test
    public void getClientList_and_renderClientList_SortByNameDesc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.name;
        options.order = "desc";

        List<Client> expectedClients = fillClientWithRNDData(101, null);
        expectedClients.sort(Comparator.comparing(o -> o.surname.toLowerCase() + o.name.toLowerCase() + o.patronymic.toLowerCase()));
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        Collections.reverse(clientRecords);
        Collections.reverse(expectedView.rowList);

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedClients.get(i).id);

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.name.toLowerCase()));
        for (int i = 0; i < expectedView.rowList.size(); i++)
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedClients.get(i).id);
    }

    @Test
    public void getClientList_and_renderClientList_SortByAgeAsc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.age;
        options.order = "asc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        expectedClients.sort(Comparator.comparing(o -> o.birth_date));
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Collections.reverse(expectedClients);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedClients.get(i).id);

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        for (int i = 0; i < expectedView.rowList.size(); i++)
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedClients.get(i).id);
    }

    @Test
    public void getClientList_and_renderClientList_SortByAgeDesc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.age;
        options.order = "desc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        expectedClients.sort(Comparator.comparing(o -> o.birth_date));
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Collections.reverse(expectedClients);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        Collections.reverse(clientRecords);
        Collections.reverse(expectedView.rowList);

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedClients.get(i).id);

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.age));
        for (int i = 0; i < expectedView.rowList.size(); i++)
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedClients.get(i).id);
    }

    @Test
    public void getClientList_and_renderClientList_SortByTotalBalanceAsc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.total;
        options.order = "asc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndTotalBalance = new HashMap<>();
        for (ClientAccount account : expectedAccounts)
            clientIdAndTotalBalance.merge(account.client, account.money, (a, b) -> a + b);
        clientIdAndTotalBalance = sortMapByValues(clientIdAndTotalBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        int i = 0;
        for (Integer key : clientIdAndTotalBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndTotalBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).total));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        i = 0;
        for (Integer key : clientIdAndTotalBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndTotalBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).total));
            i++;
        }
    }

    @Test
    public void getClientList_and_renderClientList_SortByTotalBalanceDesc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.total;
        options.order = "desc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndTotalBalance = new HashMap<>();
        for (ClientAccount account : expectedAccounts)
            clientIdAndTotalBalance.merge(account.client, account.money, (a, b) -> a + b);
        clientIdAndTotalBalance = sortMapByValues(clientIdAndTotalBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        Collections.reverse(clientRecords);
        Collections.reverse(expectedView.rowList);

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        int i = 0;
        for (Integer key : clientIdAndTotalBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndTotalBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).total));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.total));
        i = 0;
        for (Integer key : clientIdAndTotalBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndTotalBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).total));
            i++;
        }
    }

    @Test
    public void getClientList_and_renderClientList_SortByMaxBalanceAsc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.max;
        options.order = "asc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndMaxBalance = new HashMap<>();
        Float max = null;
        for (ClientAccount account : expectedAccounts) {
            Float temp = account.money;
            Float isFirstTime = clientIdAndMaxBalance.get(account.client);
            if (isFirstTime == null) {
                max = temp;
                clientIdAndMaxBalance.put(account.client, max);
            } else if (max != null && temp > max) {
                max = temp;
                clientIdAndMaxBalance.put(account.client, max);
            }
        }
        clientIdAndMaxBalance = sortMapByValues(clientIdAndMaxBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        int i = 0;
        for (Integer key : clientIdAndMaxBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndMaxBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).max));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        i = 0;
        for (Integer key : clientIdAndMaxBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndMaxBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).max));
            i++;
        }
    }

    @Test
    public void getClientList_and_renderClientList_SortByMaxBalanceDesc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.max;
        options.order = "desc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndMaxBalance = new HashMap<>();
        Float max = null;
        for (ClientAccount account : expectedAccounts) {
            Float temp = account.money;
            Float isFirstTime = clientIdAndMaxBalance.get(account.client);
            if (isFirstTime == null) {
                max = temp;
                clientIdAndMaxBalance.put(account.client, max);
            } else if (max != null && temp > max) {
                max = temp;
                clientIdAndMaxBalance.put(account.client, max);
            }
        }
        clientIdAndMaxBalance = sortMapByValues(clientIdAndMaxBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        Collections.reverse(clientRecords);
        Collections.reverse(expectedView.rowList);

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        int i = 0;
        for (Integer key : clientIdAndMaxBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndMaxBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).max));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.max));
        i = 0;
        for (Integer key : clientIdAndMaxBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndMaxBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).max));
            i++;
        }
    }

    @Test
    public void getClientList_and_renderClientList_SortByMinBalanceAsc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.min;
        options.order = "asc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndMinBalance = new HashMap<>();
        Float min = null;
        for (ClientAccount account : expectedAccounts) {
            Float temp = account.money;
            Float isFirstTime = clientIdAndMinBalance.get(account.client);
            if (isFirstTime == null) {
                min = temp;
                clientIdAndMinBalance.put(account.client, min);
            } else if (min != null && temp < min) {
                min = temp;
                clientIdAndMinBalance.put(account.client, min);
            }
        }
        clientIdAndMinBalance = sortMapByValues(clientIdAndMinBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        int i = 0;
        for (Integer key : clientIdAndMinBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndMinBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).min));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        i = 0;
        for (Integer key : clientIdAndMinBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndMinBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).min));
            i++;
        }
    }

    @Test
    public void getClientList_and_renderClientList_SortByMinBalanceDesc() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.sort = SortBy.min;
        options.order = "desc";

        List<Client> expectedClients = fillClientWithRNDData(100, null);
        List<ClientAccount> expectedAccounts = fillClientAccountWithRNDData(expectedClients);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        Map<Integer, Float> clientIdAndMinBalance = new HashMap<>();
        Float min = null;
        for (ClientAccount account : expectedAccounts) {
            Float temp = account.money;
            Float isFirstTime = clientIdAndMinBalance.get(account.client);
            if (isFirstTime == null) {
                min = temp;
                clientIdAndMinBalance.put(account.client, min);
            } else if (min != null && temp < min) {
                min = temp;
                clientIdAndMinBalance.put(account.client, min);
            }
        }
        clientIdAndMinBalance = sortMapByValues(clientIdAndMinBalance);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        Collections.reverse(clientRecords);
        Collections.reverse(expectedView.rowList);

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        int i = 0;
        for (Integer key : clientIdAndMinBalance.keySet()) {
            assertThat(key).isEqualTo(clientRecords.get(i).id);
            assertThat(Math.round(clientIdAndMinBalance.get(key))).isEqualTo(Math.round(clientRecords.get(i).min));
            i++;
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        assertThat(expectedView.rowList).isSortedAccordingTo(Comparator.comparing(o -> o.min));
        i = 0;
        for (Integer key : clientIdAndMinBalance.keySet()) {
            assertThat(key).isEqualTo(expectedView.rowList.get(i).id);
            assertThat(Math.round(clientIdAndMinBalance.get(key))).isEqualTo(Math.round(expectedView.rowList.get(i).min));
            i++;
        }
    }

    @Test
    public void getClientList_pagination_NoFilter() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.size = RND.intStr(3);
        options.page = RND.intStr(3);

        List<Client> clients = fillClientWithRNDData(1000, null);
        List<Client> expectedList = new ArrayList<>();
        int size = Integer.parseInt(options.size);
        int number = Integer.parseInt(options.page);
        int start = number * size;
        for (int i = 0; i < size; i++) {
            try {
                expectedList.add(clients.get(start));
                start++;
            } catch (Exception ex) {
                break;
            }
        }

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords.size()).isEqualTo(expectedList.size());
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedList.get(i).id);
    }

    @Test
    public void getClientList_pagination_WithFilter() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.filter = generateRndStr(10);
        options.size = RND.intStr(3);
        options.page = RND.intStr(3);

        List<Client> clients = fillClientWithRNDData(100, options.filter);
        List<Client> filteredList = new ArrayList<>();
        List<Client> expectedList = new ArrayList<>();
        int size = Integer.parseInt(options.size);
        int number = Integer.parseInt(options.page);
        int start = number * size;
        for (Client client : clients) {
            String name = client.name.toLowerCase() + client.surname.toLowerCase() + client.patronymic.toLowerCase();
            if (name.matches("(?i).*" + options.filter.toLowerCase() + ".*"))
                filteredList.add(client);
        }
        for (int i = 0; i < size; i++) {
            try {
                expectedList.add(filteredList.get(start));
                start++;
            } catch (Exception ex) {
                break;
            }
        }

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords.size()).isEqualTo(expectedList.size());
        for (ClientRecord aFilteredList : clientRecords)
            assertThat(aFilteredList.name).contains(options.filter);
        for (int i = 0; i < clientRecords.size(); i++)
            assertThat(clientRecords.get(i).id).isEqualTo(expectedList.get(i).id);
    }

    @Test(expectedExceptions = UndeclaredThrowableException.class)
    public void getClientList_pagination_wrongSize() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.filter = generateRndStr(10);
        options.size = "-254";
        options.page = RND.intStr(3);

        fillClientWithRNDData(100, options.filter);
        //
        //
        //
        clientRegister.get().getClientList(options);
    }

    @Test(expectedExceptions = UndeclaredThrowableException.class)
    public void getClientList_pagination_wrongPage() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.filter = generateRndStr(10);
        options.size = RND.intStr(3);
        options.page = "-254";

        fillClientWithRNDData(100, options.filter);
        //
        //
        //
        clientRegister.get().getClientList(options);
    }

    @Test
    public void getClientList_getClientListCount_renderClientList_FilterByName() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.filter = generateRndStr(10);
        options.size = RND.intStr(2);

        List<Client> clients = fillClientWithRNDData(100, options.filter);
        List<Client> expectedList = new ArrayList<>();
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);
        for (Client client : clients) {
            if (client.name.contains(options.filter) ||
                    client.surname.contains(options.filter) ||
                    client.patronymic.contains(options.filter))
                expectedList.add(client);
        }

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        int clientRecordsListSize = clientRegister.get().getClientListCount(options.filter);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords.size()).isLessThanOrEqualTo(Integer.parseInt(options.size));
        for (int i = 0; i < clientRecords.size(); i++) {
            assertThat(clientRecords.get(i).id).isEqualTo(expectedList.get(i).id);
            assertThat(clientRecords.get(i).name).contains(options.filter);
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        for (int i = 0; i < expectedView.rowList.size(); i++) {
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedList.get(i).id);
            assertThat(expectedView.rowList.get(i).name).contains(options.filter);
        }

        assertThat(clientRecordsListSize).isEqualTo(expectedList.size());
    }

    @Test
    public void getClientList_and_renderClientList() {
        TRUNCATE();

        RequestOptions options = new RequestOptions();
        options.size = RND.intStr(2);

        List<Client> expectedList = fillClientWithRNDData(100, options.filter);
        TestClientRecordsReportView expectedView = new TestClientRecordsReportView();
        String expectedUsername = RND.str(10);
        String expectedLink = RND.str(10);

        //
        //
        //
        List<ClientRecord> clientRecords = clientRegister.get().getClientList(options);
        clientRegister.get().renderClientList(options, expectedView, expectedUsername, expectedLink);
        //
        //
        //

        assertThat(clientRecords).isNotNull();
        assertThat(clientRecords.size()).isEqualTo(expectedList.size());
        for (int i = 0; i < clientRecords.size(); i++) {
            Client client = expectedList.get(i);
            String name = client.surname + " " + client.name + " " + client.patronymic;
            assertThat(clientRecords.get(i).id).isEqualTo(expectedList.get(i).id);
            assertThat(clientRecords.get(i).name).isEqualTo(name);
            assertThat(clientRecords.get(i).age).isEqualTo(calculateAge(client.birth_date));
            assertThat(clientRecords.get(i).charm).isEqualTo(clientTestDao.get().getCharmById(client.charm));
            assertThat(clientRecords.get(i).total).isEqualTo(0f);
            assertThat(clientRecords.get(i).max).isEqualTo(0f);
            assertThat(clientRecords.get(i).min).isEqualTo(0f);
        }

        assertThat(expectedView.rowList).isNotNull();
        assertThat(expectedView.user).isEqualTo(expectedUsername);
        assertThat(expectedView.link_to_download).isEqualTo(expectedLink);
        for (int i = 0; i < expectedView.rowList.size(); i++) {
            Client client = expectedList.get(i);
            String name = client.surname + " " + client.name + " " + client.patronymic;
            assertThat(expectedView.rowList.get(i).id).isEqualTo(expectedList.get(i).id);
            assertThat(expectedView.rowList.get(i).name).isEqualTo(name);
            assertThat(expectedView.rowList.get(i).age).isEqualTo(calculateAge(client.birth_date));
            assertThat(expectedView.rowList.get(i).charm).isEqualTo(clientTestDao.get().getCharmById(client.charm));
            assertThat(expectedView.rowList.get(i).total).isEqualTo(0f);
            assertThat(expectedView.rowList.get(i).max).isEqualTo(0f);
            assertThat(expectedView.rowList.get(i).min).isEqualTo(0f);
        }
    }

    @Test
    public void getClientDetails() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();
        Client rndClient = generateRNDClient(true, rndCharm.id, null);
        ClientAddr rndClientAddr = generateRNDClientAddr(true, rndClient.id, AddrType.REG);
        ClientPhone rndClientPhone = generateRNDClientPhone(true, rndClient.id);
        ClientDetails expectedClientDetail = generateClientDetail(rndClient, rndClientPhone, rndClientAddr);

        //
        //
        //
        ClientDetails clientDetails = clientRegister.get().getClientDetails(expectedClientDetail.id);
        //
        //
        //

        assertThat(clientDetails).isNotNull();
        assertThat(clientDetails).isEqualsToByComparingFields(expectedClientDetail);
    }

    @Test
    public void addClient_validClientData() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();
        Client rndClient = generateRNDClient(false, rndCharm.id, null);
        ClientAddr rndClientAddr = generateRNDClientAddr(false, rndClient.id, AddrType.REG);
        ClientPhone rndClientPhone = generateRNDClientPhone(false, rndClient.id);
        ClientDetails clientDetailToAdd = generateClientDetail(rndClient, rndClientPhone, rndClientAddr);
        ClientRecord expectedClientRecord = createClientRecordFromClientDetail(clientDetailToAdd, true);

        //
        //
        //
        ClientRecord clientRecord = clientRegister.get().addClient(clientDetailToAdd);
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

    @Test(expectedExceptions = PersistenceException.class)
    public void addClient_wrongCharmID() {
        TRUNCATE();

        int charmId = -254;
        Client rndClient = generateRNDClient(false, charmId, null);
        ClientAddr rndClientAddr = generateRNDClientAddr(false, rndClient.id, AddrType.REG);
        ClientPhone rndClientPhone = generateRNDClientPhone(false, rndClient.id);

        ClientDetails clientDetailToAdd = generateClientDetail(rndClient, rndClientPhone, rndClientAddr);

        //
        //
        //
        clientRegister.get().addClient(clientDetailToAdd);
    }

    @Test
    public void editClient_validClientData() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();
        Client rndClient = generateRNDClient(true, rndCharm.id, null);
        ClientAddr rndClientAddr = generateRNDClientAddr(true, rndClient.id, AddrType.REG);
        ClientPhone rndClientPhone = generateRNDClientPhone(true, rndClient.id);
        ClientDetails clientDetailToEdit = generateClientDetail(rndClient, rndClientPhone, rndClientAddr);
        ClientRecord expectedClientRecord = createClientRecordFromClientDetail(clientDetailToEdit, false);

        //
        //
        //
        ClientRecord clientRecord = clientRegister.get().editClient(clientDetailToEdit);
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

    @Test(expectedExceptions = PersistenceException.class)
    public void editClient_WrongCharmID() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();
        Client rndClient = generateRNDClient(true, rndCharm.id, null);
        ClientAddr rndClientAddr = generateRNDClientAddr(true, rndClient.id, AddrType.REG);
        ClientPhone rndClientPhone = generateRNDClientPhone(true, rndClient.id);
        ClientDetails clientDetailToEdit = generateClientDetail(rndClient, rndClientPhone, rndClientAddr);

        clientDetailToEdit.charm = -254;
        //
        //
        //
        clientRegister.get().editClient(clientDetailToEdit);
    }

    @Test
    public void deleteClient() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();
        Client rndClient = generateRNDClient(true, rndCharm.id, null);

        //
        //
        //
        clientRegister.get().deleteClient(rndClient.id);
        //
        //
        //

        Client client = clientTestDao.get().getClientById(rndClient.id);

        assertThat(client).isNull();
    }

    @Test
    public void getCharms() {
        TRUNCATE();

        Charm rndCharm = generateAndInsertRNDCharm();

        //
        //
        //
        List<Charm> charms = clientRegister.get().getCharms();
        //
        //
        //

        assertThat(charms).isNotNull();
        assertThat(charms).hasSize(1);
        assertThat(charms.get(0).id).isEqualTo(rndCharm.id);
        assertThat(charms.get(0).name).isEqualTo(rndCharm.name);
        assertThat(charms.get(0).description).isEqualTo(rndCharm.description);
        assertThat(charms.get(0).energy).isEqualTo(rndCharm.energy);
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void insertClientAddress_RepeatedCompositeKey() {
        TRUNCATE();

        Charm charm = generateAndInsertRNDCharm();
        Client client = generateRNDClient(true, charm.id, null);
        ClientAddr clientAddr = generateRNDClientAddr(false, client.id, AddrType.REG);

        //
        //
        // делаем первый инсерт
        clientTestDao.get().insert_random_client_addr(clientAddr);
        // делаем второй инсерт того же адреса, должна вылететь ошибка так как ключи повторяются
        clientTestDao.get().insert_random_client_addr(clientAddr);
    }

    @Test(expectedExceptions = PersistenceException.class)
    public void insertClientPhone_RepeatedCompositeKey() {
        TRUNCATE();

        Charm charm = generateAndInsertRNDCharm();
        Client client = generateRNDClient(true, charm.id, null);
        ClientPhone clientPhone = generateRNDClientPhone(false, client.id);

        //
        //
        // делаем первый инсерт
        clientTestDao.get().insert_random_client_phone(clientPhone);
        // делаем второй инсерт того же телефона, должна вылететь ошибка так как ключи повторяются
        clientTestDao.get().insert_random_client_phone(clientPhone);
    }

    //
    //
    //
    //
    //

    private void TRUNCATE() {
        clientTestDao.get().TRUNCATE();
    }

    private List<Client> fillClientWithRNDData(int count, String filter) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Charm rndCharm = generateAndInsertRNDCharm();
            if (filter != null && i % 3 == 0)
                clients.add(generateRNDClient(true, rndCharm.id, filter));
            else
                clients.add(generateRNDClient(true, rndCharm.id, null));
        }
        return clients;
    }

    private List<ClientAccount> fillClientAccountWithRNDData(List<Client> clients) {
        List<ClientAccount> accounts = new ArrayList<>();
        for (Client client : clients) {
            for (int i = 0; i < 5; i++) {
                ClientAccount account = generateRNDClientAccount(true, client.id);
                accounts.add(account);
            }
        }
        return accounts;
    }

    private Charm generateAndInsertRNDCharm() {
        Charm charm = new Charm();
        charm.name = RND.str(10);
        charm.description = RND.str(10);
        charm.energy = RND.plusInt(1000) * 1f;
        charm.id = clientTestDao.get().insert_random_charm(charm);
        return charm;
    }

    private Client generateRNDClient(boolean insertIt, int charmId, String filter) {
        Client client = new Client();
        client.name = generateRndStr(10);
        client.surname = generateRndStr(10);
        client.patronymic = generateRndStr(10);
        if (filter != null)
            client.name += filter;
        client.gender = randomize(Gender.class);
        client.birth_date = randomDate();
        client.charm = charmId;
        if (insertIt)
            client.id = clientTestDao.get().insert_random_client(client);
        return client;
    }

    private ClientAddr generateRNDClientAddr(boolean insertIt, int clientId, AddrType addrType) {
        ClientAddr clientAddr = new ClientAddr();
        clientAddr.client = clientId;
        clientAddr.type = addrType;
        clientAddr.street = RND.str(10);
        clientAddr.house = RND.str(10);
        clientAddr.flat = RND.str(10);
        if (insertIt)
            clientTestDao.get().insert_random_client_addr(clientAddr);
        return clientAddr;
    }

    private ClientPhone generateRNDClientPhone(boolean insertIt, int clientId) {
        ClientPhone clientPhone = new ClientPhone();
        clientPhone.client = clientId;
        clientPhone.number = RND.intStr(10);
        clientPhone.type = randomize(PhoneType.class);
        if (insertIt)
            clientTestDao.get().insert_random_client_phone(clientPhone);
        return clientPhone;
    }

    private ClientAccount generateRNDClientAccount(boolean insertIt, int clientId) {
        ClientAccount clientAccount = new ClientAccount();
        clientAccount.client = clientId;
        clientAccount.number = RND.str(10);
        clientAccount.money = RND.plusInt(1000000) * 1f;
        if (insertIt)
            clientAccount.id = clientTestDao.get().insert_random_client_account(clientAccount);
        return clientAccount;
    }

    private ClientDetails generateClientDetail(Client rndClient,
                                               ClientPhone rndClientPhone,
                                               ClientAddr rndClientAddr) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.id = rndClient.id;
        clientDetails.name = rndClient.name;
        clientDetails.surname = rndClient.surname;
        clientDetails.patronymic = rndClient.patronymic;
        clientDetails.gender = rndClient.gender.name();
        clientDetails.birth_date = rndClient.birth_date.toString();
        clientDetails.charm = rndClient.charm;
        clientDetails.addrRegStreet = rndClientAddr.street;
        clientDetails.addrRegHome = rndClientAddr.house;
        clientDetails.addrRegFlat = rndClientAddr.flat;
        ClientPhone[] phones = new ClientPhone[1];
        phones[0] = rndClientPhone;
        clientDetails.phones = phones;
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

    private Map<Integer, Float> sortMapByValues(Map<Integer, Float> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

    }

    private static class TestClientRecordsReportView implements ClientRecordsReportView {

        String user;
        Date created_at;
        String link_to_download;
        List<ClientRecordReportRow> rowList = Lists.newArrayList();

        @Override
        public void start() {
        }

        @Override
        public void append(ClientRecordReportRow row) {
            rowList.add(row);
        }

        @Override
        public void finish(String user, Date created_at, String link_to_download) {
            this.user = user;
            this.created_at = created_at;
            this.link_to_download = link_to_download;
        }

    }

}
