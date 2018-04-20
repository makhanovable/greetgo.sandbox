package kz.greetgo.sandbox.db.dao;

import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.sandbox.controller.model.Client;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientDao {

    @Select("Select * from clients where name like #{substr} or surname like #{substr} or " +
            "patronymic like #{substr}")
    List<Client> getFilteredClients(@Param("substr") String substr);

    @Select("Select * from clients where id = #{clientID}")
    Client getClient(@Param("clientID") int clientID);

    //Плохой запрос, при больших нагрузках система не выдержит
    @Delete("Delete from clients CASCADE where id = #{clientID}")
    void removeClient(@Param("clientID") int clientID);

    @Insert("insert into clients (id, surname, name, patronymic, gender, birth_date, charm_id) " +
            "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm_id}) " +
            "on conflict (id) do update " +
            "set id = #{id}, surname = #{surname}, name = #{name}, patronymic = #{patronymic}," +
            "gender = #{gender}, birth_date = #{birth_date}, charm_id = #{charm_id}")
    void insertClient(Client client);
}
