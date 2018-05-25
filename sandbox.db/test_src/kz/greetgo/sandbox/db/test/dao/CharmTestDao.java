package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import org.apache.ibatis.annotations.Insert;

public interface CharmTestDao {

  @Insert("insert into Charms ( name, description, energy ) values ( #{name}, #{description}, #{energy} )")
  void insertCharmDot(CharmDot charmDot);

}
