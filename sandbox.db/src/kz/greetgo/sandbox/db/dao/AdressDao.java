package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Adress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdressDao {

    @Select("Select * from adresses where client_id = #{clientID}")
    List<Adress> getAdress(@Param("clientID") int clientID);

    @Insert("insert into adresses (id, client_id, adressType, street, house, flat) " +
            "values (#{id} , #{clientID}, #{adressType}, #{street}, #{house}, #{flat}) on conflict (id) do update " +
            "set id = #{id}, client_id = #{clientID}, adressType = #{adressType}, " +
            "street = #{street}, house = #{house}, flat = #{flat}")
    void insertAdress(Adress adress);
}

