package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.ClientDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientTestDao {

    @Update("TRUNCATE clients CASCADE;")
    void clearClients();

    @Select("Select * from clients")
    List<ClientDot> getAllClients();

    @Insert("insert into clients (id, surname, name, patronymic, gender, birth_date, charm_id) " +
            "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm_id})")
    void insertClient(ClientDot clientDot);
}
