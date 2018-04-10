package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PhoneTestDao {

    @Update("TRUNCATE phones CASCADE;")
    void clearPhones();

    @Insert("insert into phones (client_id, phoneType, number) " +
            "values (#{clientID}, #{phoneType}, #{number})")
    void insertPhone(PhoneDot phoneDot);

    @Select("Select * from phones where client_id = #{clientID}")
    List<Phone> getPhones(@Param("clientID") int clientID);
}
