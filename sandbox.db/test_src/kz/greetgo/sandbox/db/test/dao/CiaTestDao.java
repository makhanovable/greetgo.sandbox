package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CiaTestDao {

    @Select("select * from tmp_client where cia_id = #{cia_id}")
    Client getClientByCiaId(@Param("cia_id") String cia_id);

    @Select("select * from tmp_address")
    List<Address> getAddress();

    @Select("select * from tmp_phone")
    List<Phone> getPhones();

    @Select("select * from tmp_client where cia_id = #{cia_id} and num = 1")
    Client getOldClientByCiaId(@Param("cia_id") String cia_id);

    @Select("select * from tmp_client where cia_id = #{cia_id} and num > 1")
    Client getNewClientByCiaId(@Param("cia_id") String cia_id);

}
