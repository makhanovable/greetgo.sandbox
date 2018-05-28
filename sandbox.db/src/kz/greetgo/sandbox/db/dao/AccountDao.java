package kz.greetgo.sandbox.db.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AccountDao {

    @Select("Select SUM(money) from accounts where client_id = #{clientID}")
    Float getTotalCash(@Param("clientID") int clientID);

    @Select("Select MIN(money) from accounts where client_id = #{clientID}")
    Float getMinCash(@Param("clientID") int clientID);

    @Select("Select MAX(money) from accounts where client_id = #{clientID}")
    Float getMaxCash(@Param("clientID") int clientID);
}
