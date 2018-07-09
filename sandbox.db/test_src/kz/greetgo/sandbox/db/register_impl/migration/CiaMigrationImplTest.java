package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.controller.model.ClientAddrType;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.db.migration.CIAMigration;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Phone;
import kz.greetgo.sandbox.db.test.dao.CiaTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class CiaMigrationImplTest extends ParentTestNg {

    public BeanGetter<CiaTestDao> ciaTestDao;
    private int maxBatchSize = 500_000;

    @Test
    public void insert_to_tmp_client() throws Exception {
        remove_all_data_from_tables();
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
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        Client result = ciaTestDao.get().getClientByCiaId(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.cia_id).isEqualTo(cia_id);
        assertThat(result.name).isEqualTo(name);
        assertThat(result.surname).isEqualTo(surname);
        assertThat(result.patronymic).isEqualTo(patronymic);
        assertThat(result.birth).isEqualTo(birth);
        assertThat(result.charm).isEqualTo(charm);
        assertThat(result.gender).isEqualTo(gender);
    }

    @Test
    public void insert_to_tmp_address() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_cia.xml";
        String Fstreet = "RцВWAаEkMкёнkOзДfжГк";
        String Fhouse = "RП";
        String Fflat = "hИ";
        String Rstreet = "ХfАИKлFщсiхДЗрPгWЗdЭ";
        String Rhouse = "оz";
        String Rflat = "РБ";

        Connection connection = getConnection();
        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        List<Address> result = ciaTestDao.get().getAddress();
        //
        //
        //
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        for (Address aResult : result) {
            if (aResult.type == ClientAddrType.REG) {
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
    public void insert_to_tmp_phone() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/one_cia.xml";
        String home = "+7-878-241-63-94";
        String work = "+7-418-204-55-17";
        String mobile = "+7-385-253-53-56";

        Connection connection = getConnection();
        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        List<Phone> result = ciaTestDao.get().getPhones();
        //
        //
        //
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        for (Phone aResult : result) {
            switch (aResult.type) {
                case MOBILE:
                    assertThat(aResult.phone).isEqualTo(mobile);
                    break;
                case HOME:
                    assertThat(aResult.phone).isEqualTo(home);
                    break;
                case WORK:
                    assertThat(aResult.phone).isEqualTo(work);
                    break;
            }
        }
    }

    @Test
    public void validate_tmp_client() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/invalid_one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";

        Connection connection = getConnection();
        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        Client result = ciaTestDao.get().getClientByCiaId(cia_id);
        //
        //
        //
        assertThat(result).isNotNull();
        assertThat(result.cia_id).isEqualTo(cia_id);
        assertThat(result.info).isNotNull();
        assertThat(result.name).isNull();
        assertThat(result.surname).isNull();
        assertThat(result.birth).isNull();
    }

    @Test
    public void duplacate_tmp_client() throws Exception {
        remove_all_data_from_tables();
        String file = "sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/dublicate_one_cia.xml";
        String cia_id = "0-B9N-HT-PU-04wolRBPzj";

        Connection connection = getConnection();
        //
        //
        //
        CIAMigration ciaMigration = new CIAMigration(connection, file, maxBatchSize);
        ciaMigration.migrate();
        connection.close();
        Client old = ciaTestDao.get().getOldClientByCiaId(cia_id);
        Client nw = ciaTestDao.get().getNewClientByCiaId(cia_id);
        //
        //
        //
        assertThat(old).isNotNull();
        assertThat(nw).isNotNull();
        assertThat(old.info).isEqualTo("NOT ACTUAL");
        assertThat(nw.info).isNull();
    }

    @Test
    public void insert_and_update_real_client() throws Exception {
        remove_all_data_from_tables();
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
    public void insert_and_update_real_client_addr() throws Exception {
        remove_all_data_from_tables();
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
            if (aResult.type == ClientAddrType.REG) {
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
        result = ciaTestDao.get().getRealAddress();
        //
        //
        //
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        for (ClientAddr aResult : result) {
            if (aResult.type == ClientAddrType.REG) {
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

    private void remove_all_data_from_tables() {
        ciaTestDao.get().TRUNCATE();
    }

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/makhan_sandbox",
                "makhan_sandbox",
                "111"
        );
    }

}
