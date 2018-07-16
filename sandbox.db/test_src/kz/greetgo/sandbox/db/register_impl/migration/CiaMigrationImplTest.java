package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.controller.model.AddrType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.migration.CIAMigration;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Phone;
import kz.greetgo.sandbox.db.test.dao.CiaTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.CiaGeneratorUtil;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.sandbox.db.test.util.RandomDataUtil.generateRndStr;
import static kz.greetgo.sandbox.db.test.util.RandomDataUtil.randomDate;
import static kz.greetgo.sandbox.db.test.util.RandomDataUtil.randomize;
import static org.fest.assertions.api.Assertions.assertThat;

public class CiaMigrationImplTest extends ParentTestNg {

    public BeanGetter<CiaTestDao> ciaTestDao;
    private int maxBatchSize = 500_000;
    public static BeanGetter<DbConfig> dbConfig;

    @Test
    public void isTempTablesCreated() throws Exception {
        TRUNCATE();

        Client expectedClient = generateRNDClient();
        String file = CiaGeneratorUtil.generateXmlFile(expectedClient, null, null);
        Connection connection = getConnection();
        String ex_tmp_client = "tmp_client";
        String ex_tmp_addr = "tmp_address";
        String ex_tmp_phone = "tmp_phone";

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        //
        //
        //
        String tmp_client = ciaTestDao.get().isTableExists(ex_tmp_client);
        String tmp_addr = ciaTestDao.get().isTableExists(ex_tmp_addr);
        String tmp_phone = ciaTestDao.get().isTableExists(ex_tmp_phone);

        assertThat(tmp_client).isNotNull();
        assertThat(tmp_addr).isNotNull();
        assertThat(tmp_phone).isNotNull();
        assertThat(tmp_client).isEqualTo(ex_tmp_client);
        assertThat(tmp_addr).isEqualTo(ex_tmp_addr);
        assertThat(tmp_phone).isEqualTo(ex_tmp_phone);
    }

    @Test
    public void insertingToTempClientTable() throws Exception {
        TRUNCATE();

        Client expectedClient = generateRNDClient();
        String file = CiaGeneratorUtil.generateXmlFile(expectedClient, null, null);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(expectedClient.cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.cia_id).isEqualTo(expectedClient.cia_id);
        assertThat(result.name).isEqualTo(expectedClient.name);
        assertThat(result.surname).isEqualTo(expectedClient.surname);
        assertThat(result.patronymic).isEqualTo(expectedClient.patronymic);
        assertThat(result.birth).isEqualTo(expectedClient.birth);
        assertThat(result.charm).isEqualTo(expectedClient.charm);
        assertThat(result.gender).isEqualTo(expectedClient.gender);
    }

    @Test
    public void insertingToTempClientAddressTable() throws Exception {
        TRUNCATE();

        Client expectedClient = generateRNDClient();
        List<Address> expectedAddresses = generateRNDClientAddr();
        String file = CiaGeneratorUtil.generateXmlFile(expectedClient, expectedAddresses, null);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        List<Address> result = ciaTestDao.get().getAddress();
        //
        //
        //

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(expectedAddresses.size());
        for (Address aResult : result) {
            if (aResult.type == AddrType.REG) {
                assertThat(aResult.street).isEqualTo(expectedAddresses.get(0).street);
                assertThat(aResult.house).isEqualTo(expectedAddresses.get(0).house);
                assertThat(aResult.flat).isEqualTo(expectedAddresses.get(0).flat);
            } else {
                assertThat(aResult.street).isEqualTo(expectedAddresses.get(1).street);
                assertThat(aResult.house).isEqualTo(expectedAddresses.get(1).house);
                assertThat(aResult.flat).isEqualTo(expectedAddresses.get(1).flat);
            }
        }
    }

    @Test
    public void insertingToTempClientPhoneTable() throws Exception {
        TRUNCATE();
        Client expectedClient = generateRNDClient();
        List<Phone> expectedPhones = generateRNDClientPhone();
        String file = CiaGeneratorUtil.generateXmlFile(expectedClient, null, expectedPhones);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        List<Phone> result = ciaTestDao.get().getPhones();
        //
        //
        //

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(expectedPhones.size());
        for (Phone aResult : result) {
            switch (aResult.type) {
                case MOBILE:
                    assertThat(aResult.phone).isEqualTo(result.get(2).phone);
                    break;
                case HOME:
                    assertThat(aResult.phone).isEqualTo(result.get(0).phone);
                    break;
                case WORK:
                    assertThat(aResult.phone).isEqualTo(result.get(1).phone);
                    break;
            }
        }
    }

    @Test
    public void migration_WrongName() throws Exception {
        TRUNCATE();

        Client client = generateRNDClient();
        List<Address> addrs = generateRNDClientAddr();
        List<Phone> phones = generateRNDClientPhone();

        client.name = null;

        String file = CiaGeneratorUtil.generateXmlFile(client, addrs, phones);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(client.cia_id);
        kz.greetgo.sandbox.controller.model.Client resultFromRealTable = ciaTestDao.get().getRealClientByCiaId(client.cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.status).isEqualTo(3); // error
        assertThat(resultFromRealTable).isNull();
    }

    @Test
    public void migration_WrongSurname() throws Exception {
        TRUNCATE();

        Client client = generateRNDClient();
        List<Address> addrs = generateRNDClientAddr();
        List<Phone> phones = generateRNDClientPhone();

        client.surname = null;

        String file = CiaGeneratorUtil.generateXmlFile(client, addrs, phones);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(client.cia_id);
        kz.greetgo.sandbox.controller.model.Client resultFromRealTable = ciaTestDao.get().getRealClientByCiaId(client.cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.status).isEqualTo(3); // error
        assertThat(resultFromRealTable).isNull();
    }

    @Test
    public void migration_WrongBirthDate() throws Exception {
        TRUNCATE();
        Client client = generateRNDClient();
        List<Address> addrs = generateRNDClientAddr();
        List<Phone> phones = generateRNDClientPhone();

        client.birth = null;

        String file = CiaGeneratorUtil.generateXmlFile(client, addrs, phones);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(client.cia_id);
        kz.greetgo.sandbox.controller.model.Client resultFromRealTable = ciaTestDao.get().getRealClientByCiaId(client.cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.status).isEqualTo(3); // error
        assertThat(resultFromRealTable).isNull();
    }

    @Test
    public void migration_WrongCharm() throws Exception {
        TRUNCATE();

        Client client = generateRNDClient();
        List<Address> addrs = generateRNDClientAddr();
        List<Phone> phones = generateRNDClientPhone();

        client.charm = null;

        String file = CiaGeneratorUtil.generateXmlFile(client, addrs, phones);
        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(client.cia_id);
        kz.greetgo.sandbox.controller.model.Client resultFromRealTable = ciaTestDao.get().getRealClientByCiaId(client.cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.status).isEqualTo(3); // error
        assertThat(resultFromRealTable).isNull();
    }

    @Test
    public void migrateInvalidCia() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/invalid_one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";

        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result = ciaTestDao.get().getClientByCiaId(cia_id);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.cia_id).isEqualTo(cia_id);
        assertThat(result.status).isEqualTo(3);
        assertThat(result.name).isNull();
        assertThat(result.surname).isNull();
        assertThat(result.birth).isNull();
    }

    @Test
    public void checkForDublicatesOnTempClientTable() throws Exception {
        TRUNCATE();

        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/dublicate_one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";

        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client client = ciaTestDao.get().getOldClientByCiaId(cia_id);
        //
        //
        //

        assertThat(client).isNotNull();
        assertThat(client.status).isEqualTo(2);
    }

    @Test
    public void insertAndUpdateClient() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";
        String name = "WCИTБЯ7щАо";
        String surname = "ГRнШб7gDn1";
        String patronymic = "NIfТDтуЯkТ";
        String birth = "1995-07-07";
        String charm = "ЩlВOpФpЪИШ";
        String gender = "FEMALE";

        Connection connection = getConnection();
        //
        // FIRST insertion
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        kz.greetgo.sandbox.controller.model.Client result = ciaTestDao.get().getRealClientByCiaId(cia_id);
        int charm_id = ciaTestDao.get().getRealCharmIdByName(charm);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo(name);
        assertThat(result.surname).isEqualTo(surname);
        assertThat(result.patronymic).isEqualTo(patronymic);
        assertThat(result.birth_date).isEqualTo(birth);
        assertThat(result.charm).isEqualTo(charm_id);
        assertThat(result.gender).isEqualTo(Gender.valueOf(gender));


        //
        // SECOND updating
        //
        ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        result = ciaTestDao.get().getRealClientByCiaId(cia_id);
        charm_id = ciaTestDao.get().getRealCharmIdByName(charm);
        //
        //
        //

        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo(name);
        assertThat(result.surname).isEqualTo(surname);
        assertThat(result.patronymic).isEqualTo(patronymic);
        assertThat(result.birth_date).isEqualTo(birth);
        assertThat(result.charm).isEqualTo(charm_id);
        assertThat(result.gender).isEqualTo(Gender.valueOf(gender));
    }


    @Test
    public void insertAndUpdateClientAddress() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_cia.xml";
        String Fstreet = "RцВWAаEkMкёнkOзДfжГк";
        String Fhouse = "RП";
        String Fflat = "hИ";
        String Rstreet = "ХfАИKлFщсiхДЗрPгWЗdЭ";
        String Rhouse = "оz";
        String Rflat = "РБ";

        Connection connection = getConnection();

        //
        // FIRST INSERTION
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        List<ClientAddr> result = ciaTestDao.get().getRealAddress();
        //
        //
        //
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        for (ClientAddr aResult : result) {
            if (aResult.type == AddrType.REG) {
                assertThat(aResult.street).isEqualTo(Rstreet);
                assertThat(aResult.house).isEqualTo(Rhouse);
                assertThat(aResult.flat).isEqualTo(Rflat);
            } else {
                assertThat(aResult.street).isEqualTo(Fstreet);
                assertThat(aResult.house).isEqualTo(Fhouse);
                assertThat(aResult.flat).isEqualTo(Fflat);
            }
        }

        //
        // SECOND UPDATING
        //
        ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        result = ciaTestDao.get().getRealAddress();
        //
        //
        //

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        for (ClientAddr aResult : result) {
            if (aResult.type == AddrType.REG) {
                assertThat(aResult.street).isEqualTo(Rstreet);
                assertThat(aResult.house).isEqualTo(Rhouse);
                assertThat(aResult.flat).isEqualTo(Rflat);
            } else {
                assertThat(aResult.street).isEqualTo(Fstreet);
                assertThat(aResult.house).isEqualTo(Fhouse);
                assertThat(aResult.flat).isEqualTo(Fflat);
            }
        }
    }

    @Test
    public void ciaIntegration() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/integration_one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";
        String surname = "ГRнШб7gDn1";
        String patronymic = "NIfТDтуЯkТ";
        String charm = "ЩlВOpФpЪИШ";
        String gender = "FEMALE";

        Connection connection = getConnection();

        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
        Client result_from_tmp = ciaTestDao.get().getClientByCiaId(cia_id);
        kz.greetgo.sandbox.controller.model.Client result = ciaTestDao.get().getRealClientByCiaId(cia_id);
        //
        //
        //

        assertThat(result).isNull();
        assertThat(result_from_tmp.birth).isNull();
        assertThat(result_from_tmp.surname).isEqualTo(surname);
        assertThat(result_from_tmp.patronymic).isEqualTo(patronymic);
        assertThat(result_from_tmp.charm).isEqualTo(charm);
        assertThat(result_from_tmp.gender).isEqualTo(gender);
    }

    @Test(expectedExceptions = Exception.class)
    public void migration_WrongCia() throws Exception {
        TRUNCATE();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/wrong_cia.xml";

        Connection connection = getConnection();
        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        connection = null;
    }

    private Client generateRNDClient() {
        Client client = new Client();
        client.cia_id = generateRndStr(10);
        client.name = generateRndStr(10);
        client.surname = generateRndStr(10);
        client.patronymic = generateRndStr(10);
        client.gender = randomize(Gender.class).name();
        client.birth = randomDate();
        client.charm = RND.str(10);
        return client;
    }

    private List<Address> generateRNDClientAddr() {
        List<Address> addresses = new ArrayList<>();
        Address r = new Address();
        r.type = AddrType.REG;
        r.street = RND.str(10);
        r.house = RND.str(10);
        r.flat = RND.str(10);

        Address f = new Address();
        f.type = AddrType.FACT;
        f.street = RND.str(10);
        f.house = RND.str(10);
        f.flat = RND.str(10);

        addresses.add(r);
        addresses.add(f);
        return addresses;
    }

    private List<Phone> generateRNDClientPhone() {
        List<Phone> phones = new ArrayList<>();
        Phone w = new Phone();
        w.phone = RND.intStr(10);
        w.type = PhoneType.HOME;
        phones.add(w);

        Phone m = new Phone();
        m.phone = RND.intStr(10);
        m.type = PhoneType.WORK;
        phones.add(m);

        Phone h = new Phone();
        h.phone = RND.intStr(10);
        h.type = PhoneType.MOBILE;
        phones.add(h);
        return phones;
    }

    private void TRUNCATE() {
        ciaTestDao.get().TRUNCATE();
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbConfig.get().url(),
                dbConfig.get().username(),
                dbConfig.get().password());
    }

}
