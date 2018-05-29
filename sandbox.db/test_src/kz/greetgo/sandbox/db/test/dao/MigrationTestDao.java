package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.db.migration.model.AccountJSONRecord;
import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;
import kz.greetgo.sandbox.db.migration.model.TransactionJSONRecord;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MigrationTestDao {

    @Select("Select table_name from information_schema.tables where table_name like 'cia_migration_%'")
    List<String> getCiaTableNames();

    @Select("Select COUNT(cia_id) from cia_migration_client_ where " +
            "cia_id = #{id} and name = #{name} and surname = #{surname} and gender = #{gender} and " +
            "charm = #{charm} and birth_date = #{birthDate} and fStreet = #{fStreet} and " +
            "fHouse = #{fHouse} and fFlat = #{fFlat} and rStreet = #{rStreet} and rHouse = #{rHouse} " +
            "and rFlat = #{rFlat}")
    int getCiaClient(ClientXMLRecord clientXMLRecord);

    @Select("Select cia_id from cia_migration_phone_ where " +
            "cia_id = #{id} and number = #{number} and phoneType = #{phoneType}")
    String getCiaPhone(@Param("id") String id, @Param("number") String number, @Param("phoneType") String phoneType);

    @Select("Select number from cia_migration_transaction_ where " +
            "money = #{money} and account_number = #{account_number} and finished_at = #{finished_at} " +
            "and transaction_type = #{transaction_type}")
    Long getCiaTransaction(TransactionJSONRecord transactionJSONRecord);

    @Select("Select number from cia_migration_account_ where " +
            "account_number = #{account_number} and registered_at = #{registered_at} " +
            "and client_cia_id = #{client_id}")
    Long getCiaAccount(AccountJSONRecord accountJSONRecord);

    @Select("Select status from cia_migration_client_ where " +
            "cia_id = #{id} and name = #{name} and surname = #{surname} and gender = #{gender} and " +
            "charm = #{charm} and birth_date = #{birthDate}")
    Integer getCiaClientStatus(ClientXMLRecord clientXMLRecord);

    @Select("Select status from cia_migration_phone_ where " +
            "cia_id = #{id} and number = #{number} and phoneType = #{phoneType}")
    Integer getCiaPhoneStatus(@Param("id") String id, @Param("number") String number, @Param("phoneType") String phoneType);

    @Select("Select status from cia_migration_transaction_ where " +
            "money = #{money} and account_number = #{account_number} and finished_at = #{finished_at} " +
            "and transaction_type = #{transaction_type}")
    Integer getCiaTransactionStatus(TransactionJSONRecord transactionJSONRecord);

    @Select("Select status from cia_migration_account_ where " +
            "account_number = #{account_number} and registered_at = #{registered_at} " +
            "and client_cia_id = #{client_id}")
    Integer getCiaAccountStatus(AccountJSONRecord accountJSONRecord);
}
