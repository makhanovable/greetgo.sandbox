package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface AuthTestDaoPostgres extends AuthTestDao {
  @Insert("insert into Person (id, accountName, encryptedPassword, blocked) " +
    "values (#{id}, #{accountName}, #{encryptedPassword}, #{blocked}) " +
    "ON CONFLICT (id) " +
    "DO UPDATE SET accountName = #{accountName}, encryptedPassword = #{encryptedPassword}, blocked = #{blocked}")
  void insertUser(@Param("id") String id,
                  @Param("accountName") String accountName,
                  @Param("encryptedPassword") String encryptedPassword,
                  @Param("blocked") int blocked
  );
}
