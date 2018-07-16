package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientDao {

    @Select("insert into client(surname, name, patronymic, gender, birth_date, charm) VALUES (#{client.surname}, #{client.name}, #{client.patronymic}, #{client.gender}, #{client.birth_date}, #{client.charm}) RETURNING id")
    long insert_client(@Param("client") Client client);

    @Insert("insert into client_addr VALUES (#{clientAddr.client}, #{clientAddr.type}, #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat})")
    void insert_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    @Insert("insert into client_phone VALUES (#{clientPhone.client}, #{clientPhone.number}, #{clientPhone.type})")
    void insert_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    //
    //
    //

    @Update("update client set actual = false where id = #{id}")
    void deleteClient(@Param("id") long id);

    //
    //
    //

    @Update("update client set(surname, name, patronymic, gender, birth_date, charm) = (#{client.surname}, #{client.name}, #{client.patronymic}, #{client.gender}, #{client.birth_date}, #{client.charm}) where id = #{client.id}")
    void edit_client(@Param("client") Client client);

    @Update("update client_addr set(type, street, house, flat) = (#{clientAddr.type}, #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat}) where client = #{clientAddr.client}")
    void edit_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    @Update("update client_phone set(number, type) = (#{clientPhone.number}, #{clientPhone.type}) where client = #{clientPhone.client}")
    void edit_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    //
    //
    //

    @Select("select money from client_account WHERE id = #{id}")
    List<Float> getClientAccountsMoneyById(@Param("id") long id);

    @Select("select * from client_addr where client = #{id}")
    List<ClientAddr> getClientAddrsByID(@Param("id") long id);

    @Select("select * from client_phone where client = #{id}")
    List<ClientPhone> getClientPhonesByID(@Param("id") long id);

    //
    //
    //

    @Select("select name from charm where id = #{id}")
    String getCharmById(@Param("id") long id);

    @Select("select * from client where id = #{id} and actual = true")
    Client getClientByID(@Param("id") long id);

    @Select("SELECT count(id) FROM client WHERE actual = TRUE AND (name LIKE #{filter} OR surname LIKE #{filter} OR patronymic LIKE #{filter})")
    int getClientRecordsCount(@Param("filter") String filter);

}
