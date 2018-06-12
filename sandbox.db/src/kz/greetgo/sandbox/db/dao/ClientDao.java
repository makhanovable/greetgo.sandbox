package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;

public interface ClientDao {

    @Select("insert into client(surname, name, patronymic, gender, birth_date, charm) VALUES (#{client.surname}, #{client.name}, #{client.patronymic}, #{client.gender}, #{client.birth_date}, #{client.charm}) RETURNING id")
    int insert_client(@Param("client") Client client);

    @Insert("insert into client_addr VALUES (#{clientAddr.client}, #{clientAddr.type}, #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat})")
    void insert_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    @Insert("insert into client_phone VALUES (#{clientPhone.client}, #{clientPhone.number}, #{clientPhone.type})")
    void insert_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    @Select("select name from charm where id = #{id}")
    String getCharmById(@Param("id") int id);

    @Delete("delete from client where id = #{id}")
    void deleteFromClient(@Param("id") int id);

    @Delete("delete from client_addr where client = #{id}")
    void deleteFromClientAddr(@Param("id") int id);

    @Delete("delete from client_phone where client = #{id}")
    void deleteFromClientPhone(@Param("id") int id);

    @Delete("delete from client_account where client = #{id}")
    void deleteFromClientAccount(@Param("id") int id);

    //
    //
    //

    @Select("select * from client where id = #{id}")
//    @Results({
//            @Result(property = "id", column = "id"),
//            @Result(property = "name", column = "id"),
//            @Result(property = "charm", column = "id"),
//            @Result(property = "age", column = "id"),
//            @Result(property = "total", column = "id"),
//            @Result(property = "max", column = "id"),
//            @Result(property = "min", column = "id"),
//    })
    Client getClientById(@Param("id") int id);

    @Select("select * from client_account where client = #{id}")
    ClientAccount getClientAccountById(@Param("id") int id);

}
