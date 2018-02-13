package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.AddressInfo;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.util.Util;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface ClientTestDao {
  //Client table part
  @Update("UPDATE client SET actual=0")
  void deleteAllTableClient();

  @Select("SELECT COUNT(*) FROM client WHERE actual=1")
  long selectCountTableClient();

  @Select("SELECT EXISTS (SELECT true FROM client WHERE id=#{id} AND actual=1)")
  boolean selectExistSingleTableClient(@Param("id") long id);

  @Select("SELECT nextval('client_id_seq')")
  long selectSeqIdNextValueTableClient();

  @Select("SELECT id, surname, name, patronymic, gender, " +
    "to_char(birth_date, '" + Util.datePattern + "') as birthdate, charm as charmId " +
    "FROM client " +
    "WHERE id=#{id} AND actual=1")
  ClientDetails selectRowById(@Param("id") long id);

  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm})")
  void insertClient(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm);

  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm, migration_id) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}, #{migrationId})")
  void updateClientWithMigrationId(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm,
                    @Param("migrationId") long migrationId);

  @Update("UPDATE client " +
    "SET surname=#{surname}, name=#{name}, patronymic=#{patronymic}, " +
    "gender=#{gender}, birth_date=#{birth_date}, charm=#{charm}" +
    "WHERE id=#{id}")
  void updateClient(@Param("id") long id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") String gender,
                    @Param("birth_date") Date date,
                    @Param("charm") int charm);

  //Charm table part
  @Update("UPDATE charm SET actual=0")
  void deleteAllTableCharm();

  @Select("SELECT nextval('charm_id_seq')")
  int selectSeqIdNextValueTableCharm();

  @Update("UPDATE charm SET actual=0 WHERE id=#{id}")
  void updateDisableSingleTableCharm(@Param("id") int id);

  @Insert("INSERT INTO charm (id, name, description, energy) " +
    "VALUES (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") int id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") Float energy);

  @Select("SELECT nextval('client_account_id_seq')")
  long selectSeqIdNextValueTableClientAccount();

  @Insert("INSERT INTO client_account (id, client, money, number, registered_at) " +
    "VALUES (#{id}, #{client}, #{money}, #{number}, #{registered_at})")
  void insertClientAccount(@Param("id") long id,
                           @Param("client") long client,
                           @Param("money") float money,
                           @Param("number") String number,
                           @Param("registered_at") Timestamp registered_at);

  @Select("SELECT street, house, flat, type " +
    "FROM client_addr " +
    "WHERE client=#{client} AND type=#{type}")
  AddressInfo selectRowByClientAndTypeTableClientAddr(@Param("client") long client, @Param("type") String type);

  @Insert("INSERT INTO client_addr (client, type, street, house, flat) " +
    "VALUES (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddr(@Param("client") long client,
                        @Param("type") String type,
                        @Param("street") String street,
                        @Param("house") String house,
                        @Param("flat") String flat);

  //Client_Phone table part
  @Update("UPDATE client_phone SET actual=0")
  void deleteAllTableClientPhone();

  @Select("SELECT number, type FROM client_phone WHERE client=#{client} AND actual=1")
  List<Phone> selectRowsByClientTableClientPhone(@Param("client") long client);

  @Insert("INSERT INTO client_phone (client, number, type) " +
    "VALUES (#{client}, #{number}, #{type})")
  void insertClientPhone(@Param("client") long client,
                         @Param("number") String number,
                         @Param("type") String type);
}
