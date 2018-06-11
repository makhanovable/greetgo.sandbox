package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

}
