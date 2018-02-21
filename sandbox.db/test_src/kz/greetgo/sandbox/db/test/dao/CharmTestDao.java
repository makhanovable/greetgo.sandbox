package kz.greetgo.sandbox.db.test.dao;


import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmTestDao {

  @Insert("insert into Charm (id, name, description, energy) " +
    "values (#{id}, #{name}, #{description}, #{energy})")
  void insertCharmDot(CharmDot charmDot);

  @Select("select id, name, description, energy from Charm")
  List<CharmDot> getAll();

  @Insert("TRUNCATE Charm")
  void clear();
}
