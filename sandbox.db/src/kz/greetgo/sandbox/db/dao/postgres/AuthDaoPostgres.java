package kz.greetgo.sandbox.db.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.auth.model.UserParamName;
import kz.greetgo.sandbox.db.dao.AuthDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface AuthDaoPostgres extends AuthDao {
  @Insert("insert into UserParams (personId, name, value) values (#{personId}, #{name}, #{value})" +
    " on conflict (personId, name) do update set value = #{value}")
  void saveUserParam(@Param("personId") String personId,
                     @Param("name") UserParamName name,
                     @Param("value") String value);
}
