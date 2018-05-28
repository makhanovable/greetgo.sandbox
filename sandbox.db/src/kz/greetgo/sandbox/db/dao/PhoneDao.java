package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PhoneDao {

    @Select("Select * from phones where client_id = #{clientID}")
    List<Phone> getPhones(@Param("clientID") int clientID);

    @Insert("insert into phones (client_id, phoneType, number) " +
            "values (#{clientID}, #{phoneType}, #{number}) on conflict (number) do update " +
            "set client_id = #{clientID}, phoneType = #{phoneType}, number = #{number}")
    void insertPhone(Phone phone);
}
