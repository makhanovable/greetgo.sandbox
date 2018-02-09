package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Charm;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CharmDao {
  int selectFirstRowId();

  @Select("SELECT id, name FROM charm WHERE actual=1")
  List<Charm> selectActualCharms();
}
