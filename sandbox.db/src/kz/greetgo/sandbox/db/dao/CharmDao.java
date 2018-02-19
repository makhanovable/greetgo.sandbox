package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.CharmRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {
  @Select("select id, name from Charm")
  List<CharmRecord> getAll();
}
