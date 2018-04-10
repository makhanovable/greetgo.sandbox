package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {
    @Select("Select * from charms")
    List<Charm> getAllCharms();

    @Select("Select name from charms where id = #{charmID}")
    String getCharm(@Param("charmID") int charmID);
}
