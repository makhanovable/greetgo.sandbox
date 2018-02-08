package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.dao.CharmDao;
import org.apache.ibatis.annotations.Select;

@Bean
public interface CharmDaoPostgres extends CharmDao {
  @Select("SELECT ciaId FROM charm WHERE actual=1 ORDER BY ciaId ASC LIMIT 1")
  int selectFirstRowId();
}
