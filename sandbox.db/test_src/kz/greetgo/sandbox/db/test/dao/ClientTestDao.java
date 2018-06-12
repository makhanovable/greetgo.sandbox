package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

public interface ClientTestDao {

    @Insert("TRUNCATE client, client_addr, client_phone,client_account, client_account_transaction,transaction_type, charm CASCADE")
    void TRUNCATE();

    @Select("insert into client(surname, name, patronymic, gender, birth_date, charm) VALUES (#{client.surname}, #{client.name}, #{client.patronymic}, #{client.gender}, #{client.birth_date}, #{client.charm}) RETURNING id")
    int insert_random_client(@Param("client") Client client);

    @Insert("insert into client_addr VALUES (#{clientAddr.client}, #{clientAddr.type}, #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat})")
    void insert_random_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    @Insert("insert into client_phone VALUES (#{clientPhone.client}, #{clientPhone.number}, #{clientPhone.type})")
    void insert_random_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    @Insert("insert into client_account(client, money, number, registered_at) VALUES (#{clientAccount.client}, #{clientAccount.money}, #{clientAccount.number}, #{clientAccount.registered_at})")
    void insert_random_client_account(@Param("clientAccount") ClientAccount clientAccount);

    @Insert("insert into client_account_transaction VALUES (#{clientAccountTransaction.id}, #{clientAccountTransaction.account}, #{clientAccountTransaction.money}, #{clientAccountTransaction.finished_at}, #{clientAccountTransaction.type})")
    void insert_random_client_account_transaction(@Param("clientAccountTransaction") ClientAccountTransaction clientAccountTransaction);

    @Insert("insert into transaction_type VALUES (#{transactionType.id}, #{transactionType.code}, #{transactionType.name})")
    void insert_random_transaction_type(@Param("transactionType") TransactionType transactionType);

    @Insert("insert into charm VALUES (#{charm.id}, #{charm.name}, #{charm.description}, #{charm.energy})")
    void insert_random_charm(@Param("charm") Charm charm);

    //
    //
    //

    @Select("select count(id) from charm")
    Integer getCharmsCount();

    @Select("select name from charm where id = #{id}")
    String getCharmById(@Param("id") int id);

    @Select("select * from client where id = #{id}")
    Client getClientById(@Param("id") int id);

    @Select("select * from client_addr where client = #{id}")
    List<ClientAddr> getClientAddrsById(@Param("id") int id);

    @Select("select * from client_phone where client = #{id}")
    List<ClientPhone> getClientPhonesById(@Param("id") int id);

    @Select("select * from client_account where client = #{id}")
    List<ClientAccount> getClientAccountsById(@Param("id") int id);

    @Select("select * from client_account_transaction where id = #{id}")
    List<ClientAccountTransaction> getClientAccountTransactionsById(@Param("id") int id);

    //
    //
    //

    @Select("select money from client_account WHERE id = #{id}")
    Float getTotalBalanceById(@Param("id") int id);

    @Select("select money from client_account WHERE id = #{id}") // TODO calculate min
    Float getMinBalanceById(@Param("id") int id);

    @Select("select money from client_account WHERE id = #{id}") // TODO calculate max
    Float getMaxBalanceById(@Param("id") int id);

}
