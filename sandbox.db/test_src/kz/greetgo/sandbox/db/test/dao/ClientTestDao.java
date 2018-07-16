package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientTestDao {

    //language=PostgreSQL
    @Insert("TRUNCATE client, client_addr, client_phone, client_account, client_account_transaction, transaction_type, charm CASCADE")
    void TRUNCATE();

    //language=PostgreSQL
    @Select("INSERT INTO charm (name, description, energy) VALUES " +
            "(#{charm.name}, #{charm.description}, #{charm.energy}) RETURNING id")
    Long insert_random_charm(@Param("charm") Charm charm);

    //language=PostgreSQL
    @Select("INSERT INTO client (surname, name, patronymic, gender, birth_date, charm) VALUES " +
            "(#{client.surname}, #{client.name}, #{client.patronymic}," +
            "#{client.gender}, #{client.birth_date}, #{client.charm}) RETURNING id")
    Long insert_random_client(@Param("client") Client client);

    //language=PostgreSQL
    @Insert("INSERT INTO client_addr VALUES (#{clientAddr.client}, #{clientAddr.type}," +
            " #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat})")
    void insert_random_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    //language=PostgreSQL
    @Insert("INSERT INTO client_phone VALUES (#{clientPhone.client}, #{clientPhone.number}," +
            " #{clientPhone.type})")
    void insert_random_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    //language=PostgreSQL
    @Select("insert into client_account(client, money, number) VALUES (#{clientAccount.client}, #{clientAccount.money}, #{clientAccount.number}) RETURNING id")
    Long insert_random_client_account(@Param("clientAccount") ClientAccount clientAccount);

    @Insert("insert into client_account_transaction(account, money, type) VALUES (#{clientAccountTransaction.account}, #{clientAccountTransaction.money}, #{clientAccountTransaction.type})")
    void insert_random_client_account_transaction(@Param("clientAccountTransaction") ClientAccountTransaction clientAccountTransaction);

    @Insert("insert into transaction_type VALUES (#{transactionType.id}, #{transactionType.code}, #{transactionType.name})")
    void insert_random_transaction_type(@Param("transactionType") TransactionType transactionType);

    //
    //
    //

    @Select("select count(id) from charm")
    Integer getCharmsCount();

    @Select("select name from charm where id = #{id}")
    String getCharmById(@Param("id") long id);

    @Select("select * from client where id = #{id} and actual = true")
    Client getClientById(@Param("id") long id);

    @Select("select * from client_addr where client = #{id}")
    List<ClientAddr> getClientAddrsById(@Param("id") long id);

    @Select("select * from client_phone where client = #{id}")
    List<ClientPhone> getClientPhonesById(@Param("id") long id);

    @Select("select * from client_account where client = #{id}")
    List<ClientAccount> getClientAccountsById(@Param("id") long id);

    @Select("select id from client where actual = true")
    List<Integer> getAllActualClientIds();

    //
    //
    //

    @Select("select money from client_account WHERE id = #{id}")
    List<Float> getClientAccountsMoneyById(@Param("id") long id);

}
