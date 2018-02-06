package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.util.Util;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Date;

public interface ClientDao {
  @Update("UPDATE client SET actual=0 WHERE id=#{id}")
  void deleteRowById(@Param("id") long id);

  @Select("SELECT EXISTS (SELECT true FROM client WHERE id=#{id} AND actual=1)")
  boolean selectExistsRowById(@Param("id") long id);

  @Select("SELECT id, surname, name, patronymic, gender, " +
    "to_char(birth_date, '" + Util.datePattern + "') as birthdate, charm as charmId " +
    "FROM client " +
    "WHERE id=#{id} AND actual=1")
  ClientDetails selectDetailsById(@Param("id") long id);

  @Select("SELECT nextval('client_id_seq')")
  long selectNextIdSeq();

  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES(#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insert(@Param("id") long id,
              @Param("surname") String surname,
              @Param("name") String name,
              @Param("patronymic") String patronymic,
              @Param("gender") String gender,
              @Param("birth_date") Date birthdate,
              @Param("charm") int charm);

  @Update("UPDATE client " +
    "SET surname=#{surname}, name=#{name}, patronymic=#{patronymic}, " +
    "gender=#{gender}, birth_date=#{birth_date}, charm=#{charm}" +
    "WHERE id=#{id}")
  void update(@Param("id") long id,
              @Param("surname") String surname,
              @Param("name") String name,
              @Param("patronymic") String patronymic,
              @Param("gender") String gender,
              @Param("birth_date") Date birthdate,
              @Param("charm") int charm);
}
