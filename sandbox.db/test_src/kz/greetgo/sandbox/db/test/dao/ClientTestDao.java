package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface ClientTestDao {

  @Insert("insert into Client (id, name, surname, patronymic, gender, birthDate, charm) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birthDate}, #{charm})")
  void insertClientDot(ClientDot clientDot);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void insertPhone(ClientPhoneNumberDot phone);

  @Insert("insert into ClientAddr (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(ClientAddressDot address);

  @Select("TRUNCATE Client; TRUNCATE ClientPhone; TRUNCATE ClientAddr")
  void clear();
}
