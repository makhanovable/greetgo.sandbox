package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AuthDao {
  void saveUserParam(String personId, UserParamName name, String value);

  @Select("select value from UserParams where personId = #{personId} and name = #{name}")
  String getUserParam(@Param("personId") String personId,
                      @Param("name") UserParamName name);

  @Select("select ciaId from Person where accountName = #{accountName} " +
    "and encryptedPassword = #{encryptedPassword} and blocked = 0")
  String selectPersonIdByAccountAndPassword(@Param("accountName") String accountName,
                                            @Param("encryptedPassword") String encryptedPassword);

  @Select("select accountName from Person where ciaId = #{personId}")
  String accountNameByPersonId(@Param("personId") String personId);

  @Select("select * from Person where ciaId = #{personId}")
  UserInfo getUserInfo(@Param("personId") String personId);
}
