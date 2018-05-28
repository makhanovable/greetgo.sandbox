package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Adress;
import kz.greetgo.sandbox.db.stand.model.AdressDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AdressTestDao {

    @Update("TRUNCATE adresses CASCADE;")
    void clearAdresses();

    @Insert("insert into adresses (id, client_id, adressType, street, house, flat) " +
            "values (#{id}, #{clientID}, #{adressType}, #{street}, #{house}, #{flat})")
    void insertAdress(AdressDot adressDot);

    @Select("Select * from adresses where client_id = #{clientID}")
    List<Adress> getAdress(@Param("clientID") int clientID);
}
