package kz.greetgo.sandbox.db.test.dao;


import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Insert;

public interface CharmTestDao {

  @Insert("insert into Charm (id, name, description, energy) " +
    "values (#{id}, #{name}, #{description}, #{energy})")
  void insertClientDot(CharmDot charmDot);

  @Insert("TRUNCATE Charm")
  void clear();
}
