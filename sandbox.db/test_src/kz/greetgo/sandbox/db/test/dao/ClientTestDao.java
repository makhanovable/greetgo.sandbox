package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.model.ClientCia;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientTestDao {


  @Select("Select id, name, surname, patronymic, birthDate, gender, charm from client where name=#{name}")
  List<ClientDetail> getAllByName(@Param("name") String name);

  @Insert("insert into Client (id, cia_id, name, surname, patronymic, gender, birthDate, charm) " +
    "values (#{id}, #{cia_id}, #{name}, #{surname}, #{patronymic}, #{gender}, #{birthDate}, #{charm})")
  void insertClientDot(ClientDot clientDot);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void insertPhone(ClientPhoneNumberDot phone);

  @Insert("insert into ClientAddr (client, cia_id, type, street, house, flat) " +
    "values (#{client}, #{cia_id}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(ClientAddressDot address);

  @SuppressWarnings("SameParameterValue")
  @Select("select id, name, surname, patronymic, birthDate, gender, charm from client where id=#{id} and actual=#{actual}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "phoneNumbers", column = "id", javaType = List.class,
      many = @Many(select = "getNumbersByIdOrderByNumber"))
  })
  ClientDetail detail(@Param("id") String id, @Param("actual") Boolean actual);


  @Select("select client, number, type from ClientPhone where client=#{client} order by number")
  List<ClientPhoneNumber> getNumbersByIdOrderByNumber(String client);

  @Select("select client, type, street, house, flat from ClientAddr where client=#{client} and type=#{type}")
  ClientAddress getAddres(@Param("client") String client, @Param("type") AddressType type);


  @SuppressWarnings("SameParameterValue")
  @Select("select cia_id as id, name, surname, patronymic, birthDate, gender, charm from ${clientTableName}")
  List<ClientDetail> getClientTestList(@Param("clientTableName") String clientTableName);

  @SuppressWarnings("SameParameterValue")
  @Select("select cia_id as id, name, surname, patronymic, birthDate, gender, charm from ${clientTableName} where cia_id=#{ciaId}")
  ClientDetail getClientByCiaId(@Param("clientTableName") String clientTableName, @Param("ciaId") String ciaId);


  @SuppressWarnings("SameParameterValue")
  @Select("select id, cia_id, name, surname, patronymic, birthDate, gender, charm, error from ${clientTableName}")
  List<ClientCia> getTempClientList(@Param("clientTableName") String clientTableName);

  @Select("select number, type from ${tableName} where client_id=#{id}")
  List<ClientPhoneNumber> getNumberTempTableList(@Param("tableName") String tableName, @Param("id") String client);

  @Select("select type, street, house, flat from ${tableName} where client_id=#{id}")
  List<ClientAddress> getTempAddressList(@Param("tableName") String tableName, @Param("id") String client);

  @Select("select type, street, house, flat from ${tableName} where cia_id=#{id} and type=#{type}")
  ClientAddress getRealAddressByType(@Param("tableName") String tableName, @Param("id") String client, @Param("type") String type);


  @Select("drop table if exists ${tableName}")
  void dropTable(@Param("tableName") String tableName);

  @Select("TRUNCATE Client cascade; TRUNCATE ClientPhone cascade; TRUNCATE ClientAddr cascade; TRUNCATE charm cascade;")
  void clear();

  @Select("select name from charm where id=#{id}")
  String getCharmNameById(@Param("id") String id);


  @Insert("INSERT INTO ${tableName} (id, cia_id, name, surname, patronymic, gender, birthDate, charm, error) VALUES " +
    "(#{detail.id}, #{detail.cia_id}, #{detail.name}, #{detail.surname}, #{detail.patronymic}, #{detail.gender}, #{detail.birthDate}, #{detail.charm}, #{detail.error})")
  void insertClientDetail(@Param("detail") ClientCia detail, @Param("tableName") String tableName);

  @Insert("insert into ${tableName} (client_id, number, type) " +
    "values (#{phone.client}, #{phone.number}, #{phone.type})")
  void insertPhoneIntoTemp(@Param("phone") ClientPhoneNumber phone, @Param("tableName") String tableName);


  @Insert("insert into ${tableName} (client_id, type, street, house, flat) " +
    "values (#{addr.client}, #{addr.type}, #{addr.street}, #{addr.house}, #{addr.flat})")
  void insertAddressIntoTemp(@Param("addr") ClientAddress addr, @Param("tableName") String tableName);

  @SuppressWarnings("SameParameterValue")
  @Select("select id, cia_id, name, surname, patronymic, birthDate, gender, charm, error from ${clientTableName} where error NOTNULL")
  List<ClientCia> getTempClientListWithErrors(@Param("clientTableName") String clientTableName);

  @Select("select bool_and(actual) from client where mig_id=#{migId}")
  boolean isAllRowsActualTrueWhereMigId(@Param("migId") String migId);


}
