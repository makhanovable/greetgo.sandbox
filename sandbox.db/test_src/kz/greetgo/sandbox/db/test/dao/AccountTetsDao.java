package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import org.apache.ibatis.annotations.Insert;

@Bean
public interface AccountTetsDao {

  @Insert("insert into ClientAccount (id, client, money, number, registeredAt) " +
    "values (#{id}, #{client}, #{money}, #{number}, #{registeredAt})")
  public void insertAccaount(ClientAccountDot accountDot);

}
