package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;

public interface TempSessionDao {
  @Insert("INSERT INTO temp_session (token, person_id, url, lifetime) " +
    "VALUES (#{token}, #{person_id}, #{url}, #{lifetime}")
  void insert(@Param("token") String token,
              @Param("person_id") String person_id,
              @Param("url") String url,
              @Param("lifetime") Timestamp lifetime);

  @Select("SELECT lifetime " +
    "FROM temp_session " +
    "WHERE token = #{token}, person_id = #{person_id}, EXTRACT (EPOCH FROM (curtime - lifetime)) => 0")
  void selectLifetime(@Param("token") String token,
                      @Param("person_id") String person_id,
                      @Param("url") String url,
                      @Param("lifetime") Timestamp lifetime);

  @Delete("DELETE temp_session WHERE token = #{token}")
  void delete();
}
