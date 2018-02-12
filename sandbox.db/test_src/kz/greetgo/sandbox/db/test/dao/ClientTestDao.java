package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.ClientDot;
import org.apache.ibatis.annotations.Insert;

public interface ClientTestDao {

  @Insert("insert into Client (id, name, surname, patronymic, gender, birthDate, charm) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birthDate}, #{charmId})")
  void insertClientDot(ClientDot clientDot);


}
