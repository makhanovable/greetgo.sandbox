package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

public interface ClientDao {

    @Insert("insert into client VALUES (#{client.id}, #{client.surname},#{client.name},#{client.patronymic},#{client.gender},#{client.birth_date},#{client.charm})")
    void insert_client(@Param("client") Client client);

    @Insert("insert into client_addr VALUES (#{clientAddr.client}, #{clientAddr.type}, #{clientAddr.street}, #{clientAddr.house}, #{clientAddr.flat})")
    void insert_client_addr(@Param("clientAddr") ClientAddr clientAddr);

    @Insert("insert into client_phone VALUES (#{clientPhone.client}, #{clientPhone.number}, #{clientPhone.type})")
    void insert_client_phone(@Param("clientPhone") ClientPhone clientPhone);

    @Insert("insert into client_account VALUES (#{clientAccount.id}, #{clientAccount.client}, #{clientAccount.money}, #{clientAccount.number}, #{clientAccount.registered_at})")
    void insert_client_account(@Param("clientAccount") ClientAccount clientAccount);

    @Select("select name from charm where id = #{id}")
    String getCharmById(@Param("id") int id);

    @Select("select id from id")
    Integer getLastID();

    @Update("update id set id = #{id}")
    void setLastID(@Param("id") int id);

    @Insert("insert into id values(#{id})")
    void insertID(@Param("id") int id);

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
