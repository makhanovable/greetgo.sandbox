package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CharmTestDao {

    @Update("TRUNCATE charms CASCADE;")
    void clearCharms();

    @Select("Select * from charms")
    List<Charm> getAllCharms();

    @Insert("insert into charms (id, name, description, energy) values (#{id}, #{name}, #{description}, #{energy})")
    void insertCharm(CharmDot charmDot);
}
