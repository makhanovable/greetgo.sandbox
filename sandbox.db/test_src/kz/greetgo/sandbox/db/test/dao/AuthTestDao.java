package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AuthTestDao {
  @Select("select value from UserParams where personId = #{personId} and name = #{name}")
  String loadParamValue(@Param("personId") String personId, @Param("name") UserParamName paramName);

  @Select("select count(1) from UserParams where personId = #{personId} and name = #{name} and value is not null")
  int countOfUserParams(@Param("personId") String personId, @Param("name") UserParamName paramName);

  @Insert("insert into UserParams (personId, name, value) values (#{personId}, #{name}, #{value})")
  void insertUserParam(@Param("personId") String personId,
                       @Param("name") UserParamName name,
                       @Param("value") String value);

  void insertUser(@Param("ciaId") String id,
                  @Param("accountName") String accountName,
                  @Param("encryptedPassword") String encryptedPassword,
                  @Param("blocked") int blocked
  );

  @Update("update Person set ${fieldName} = #{fieldValue} where ciaId = #{ciaId}")
  void updatePersonField(@Param("ciaId") String id,
                         @Param("fieldName") String fieldName,
                         @Param("fieldValue") Object fieldValue);

  @Insert("insert into Person (  ciaId,    accountName,    surname,    name,    patronymic,    encryptedPassword, blocked) " +
    "                  values (#{ciaId}, #{accountName}, #{surname}, #{name}, #{patronymic}, #{encryptedPassword}, 0)")
  void insertPersonDot(PersonDot personDot);

  @Update("UPDATE Person SET blocked=1")
  void deleteAllTablePerson();
}
