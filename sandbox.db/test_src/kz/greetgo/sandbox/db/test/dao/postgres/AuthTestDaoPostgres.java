package kz.greetgo.sandbox.db.test.dao.postgres;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Bean
public interface AuthTestDaoPostgres extends AuthTestDao {
  @Insert("insert into Person (ciaId, accountName, encryptedPassword, blocked) " +
    "values (#{ciaId}, #{accountName}, #{encryptedPassword}, #{blocked}) " +
    "ON CONFLICT (ciaId) " +
    "DO UPDATE SET accountName = #{accountName}, encryptedPassword = #{encryptedPassword}, blocked = #{blocked}")
  void insertUser(@Param("ciaId") String id,
                  @Param("accountName") String accountName,
                  @Param("encryptedPassword") String encryptedPassword,
                  @Param("blocked") int blocked
  );
}
