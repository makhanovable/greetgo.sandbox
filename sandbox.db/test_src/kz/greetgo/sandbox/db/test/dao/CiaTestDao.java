package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientAddr;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CiaTestDao {

    @Insert("TRUNCATE client, client_addr, client_phone,client_account, client_account_transaction,transaction_type, charm CASCADE")
    void TRUNCATE();

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

    //
    //
    //
    @Select("select * from client where cia_client_id = #{cia_id}")
    kz.greetgo.sandbox.controller.model.Client getRealClientByCiaId(@Param("cia_id") String cia_id);

    @Select("select id from charm where name = #{name}")
    int getRealCharmIdByName(@Param("name") String name);

    @Select("select * from client_addr")
    List<ClientAddr> getRealAddress();

    @Select("SELECT table_name FROM information_schema.tables WHERE table_name = #{table}")
    String isTableExists(@Param("table") String table);
}
