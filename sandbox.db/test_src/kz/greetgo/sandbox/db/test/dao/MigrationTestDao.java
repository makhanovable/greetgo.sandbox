package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface MigrationTestDao {
  @Insert("INSERT INTO ${tmpClientTableName} " +
    "VALUES (#{instanceId}, #{ciaId}, #{surname}, #{name}, #{patronymic}, #{gender}, #{charm_name}, #{charm_id}, " +
    "#{birth_date}, #{status}, #{error})")
  void insertClient(@Param("tmpClientTableName") String tmpClientTableName,
                    @Param("instanceId") long instanceId,
                    @Param("ciaId") String ciaId,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("charm_name") String charm_name,
                    @Param("charm_id") Integer charm_id,
                    @Param("birth_date") String birth_date,
                    @Param("status") int status,
                    @Param("error") String error);
}
