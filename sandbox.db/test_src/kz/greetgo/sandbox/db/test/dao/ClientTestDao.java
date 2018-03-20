package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.model.Client;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import static kz.greetgo.sandbox.db.register_impl.migration.enums.MigrationStatus.NOT_READY;
import static kz.greetgo.sandbox.db.register_impl.migration.enums.TmpTableName.*;

public interface ClientTestDao {


  @Select("Select id, name, surname, patronymic, birthDate, gender, charm from client where name=#{name}")
  List<ClientDetail> getAllByName(@Param("name") String name);

  @Insert("insert into Client (id, name, surname, patronymic, gender, birthDate, charm) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birthDate}, #{charm})")
  void insertClientDot(ClientDot clientDot);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void insertPhone(ClientPhoneNumberDot phone);

  @Insert("insert into ClientAddr (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(ClientAddressDot address);

  @SuppressWarnings("SameParameterValue")
  @Select("select id, name, surname, patronymic, birthDate, gender, charm from client where id=#{id} and actual=#{actual}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "phoneNumbers", column = "id", javaType = List.class,
      many = @Many(select = "getNumbersById"))
  })
  ClientDetail detail(@Param("id") String id, @Param("actual") Boolean actual);


  @Select("select client, number, type from ClientPhone where client=#{client}")
  List<ClientPhoneNumber> getNumbersById(String client);

  @Select("select client, type, street, house, flat from ClientAddr where client=#{client} and type=#{type}")
  ClientAddress getAddres(@Param("client") String client, @Param("type") AddressType type);


  @SuppressWarnings("SameParameterValue")
  @Select("select cia_id as id, name, surname, patronymic, birthDate, gender, charm from ${clientTableName}")
  List<ClientDetail> getClientTestList(@Param("clientTableName") String clientTableName);

  @SuppressWarnings("SameParameterValue")
  @Select("select id, cia_id, name, surname, patronymic, birthDate, gender, charm, error from ${clientTableName}")
  List<Client> getTempClientList(@Param("clientTableName") String clientTableName);

  @Select("select number, type from ${tableName} where client_id=#{id}")
  List<ClientPhoneNumber> getNumberList(@Param("tableName") String tableName, @Param("id") String client);

  @Select("select type, street, house, flat from ${tableName} where client_id=#{id}")
  List<ClientAddress> getAddressList(@Param("tableName") String tableName, @Param("id") String client);


  @Select("drop table if exists ${tableName}")
  void dropTable(@Param("tableName") String tableName);

  @Select("TRUNCATE Client cascade; TRUNCATE ClientPhone cascade; TRUNCATE ClientAddr cascade")
  void clear();


  @Select("create table ${tableName} (\n" +
    "  no bigserial,\n" +
    "  id varchar(32),\n" +
    "  cia_id varchar(100),\n" +
    "  client_id varchar(100),\n" +
    "  name varchar(255),\n" +
    "  surname varchar(255),\n" +
    "  patronymic varchar(255),\n" +
    "  gender varchar(10),\n" +
    "  birthDate varchar(20),\n" +
    "  birthDateParsed date,\n" +
    "  charm varchar(32),\n" +
    "  actual boolean default false,\n" +
    "  error varchar(100),\n" +
    "  mig_status smallint default 1,\n" +
    "  PRIMARY KEY (no)\n" +
    ")")
  void createTempClientTable(@Param("tableName") String tableName);

  @Select("create table ${tableName} (\n " +
    "  client_id varchar(32),\n" +
    "  type varchar(100),\n" +
    "  street varchar(100),\n" +
    "  house varchar(100),\n" +
    "  flat varchar(100)\n" +
    ")\n")
  void createTempAddressTable(@Param("tableName") String tableName);

  @Select("create table ${tableName} (\n" +
    "  client_id varchar(32),\n" +
    "  number varchar(100),\n" +
    "  type varchar(100)\n" +
    ")\n")
  void createTempPhoneTable(@Param("tableName") String tableName);

  @Select("SELECT EXISTS (\n" +
    "   SELECT 1\n" +
    "   FROM   information_schema.tables \n" +
    "   WHERE    table_name = #{tableName}\n" +
    "   );")
  boolean isTableExist(@Param("tableName") String tableName);

  @Insert("INSERT INTO ${tableName} (id, cia_id, name, surname, patronymic, gender, birthDate, charm, error) VALUES " +
    "(#{detail.id}, #{detail.cia_id}, #{detail.name}, #{detail.surname}, #{detail.patronymic}, #{detail.gender}, #{detail.birthDate}, #{detail.charm}, #{detail.error})")
  void insertClientDetail(@Param("detail") Client detail, @Param("tableName") String tableName);

  @Insert("insert into ${tableName} (client_id, number, type) " +
    "values (#{phone.client}, #{phone.number}, #{phone.type})")
  void insertPhoneIntoTemp(@Param("phone") ClientPhoneNumber phone, @Param("tableName") String tableName);


  @Insert("insert into ${tableName} (client_id, type, street, house, flat) " +
    "values (#{addr.client}, #{addr.type}, #{addr.street}, #{addr.house}, #{addr.flat})")
  void insertAddressIntoTemp(@Param("addr") ClientAddress addr, @Param("tableName") String tableName);

  @SuppressWarnings("SameParameterValue")
  @Select("select id, cia_id, name, surname, patronymic, birthDate, gender, charm, error from ${clientTableName} where error NOTNULL")
  List<Client> getTempClientListWithErrors(@Param("clientTableName") String clientTableName);

}
