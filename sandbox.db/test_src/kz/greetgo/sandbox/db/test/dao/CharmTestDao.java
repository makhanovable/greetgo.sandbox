package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmTestDao {

    @Select("Select * from charm")
    List<Charm> getAll();

    @Insert("Insert into charm (id, name, description, energy) values (#{id}, #{name}, #{desc}, #{energy})")
    void insert(Charm charm);
}
