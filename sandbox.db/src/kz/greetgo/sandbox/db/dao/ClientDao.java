package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@SuppressWarnings("SameParameterValue")
public interface ClientDao {

  @Select("select count(1) from Client c where lower(concat(c.name, c.surname, c.patronymic)) like #{filter}")
  int countByFilter(@Param("filter") String filter);

  @Select("select count(1) from Client")
  int countAll();

  @Select("select id, name, surname, patronymic, birthDate, gender, charm from client where id=#{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "phoneNumbers", column = "id", javaType = List.class,
      many = @Many(select = "getNumbersById"))
  })
  ClientDetail detail(@Param("id") String id);

  @Select("select client, number, type from ClientPhone where client=#{client}")
  List<ClientPhoneNumber> getNumbersById(String client);

  @Select("select client, type, street, house, flat from ClientAddr where client=#{client} and type=#{type}")
  ClientAddress getAddres(@Param("client") String client, @Param("type") AddressType type);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void insertPhone(ClientPhoneNumber phone);

  @Insert("insert into ClientAddr (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(ClientAddress address);

  @Insert("insert into Client (id, name, surname, patronymic, birthDate, gender, charm) " +
    "values (#{id}, #{name}, #{surname}, #{patronymic}, #{birthDate}, #{gender}, #{charm})")
  void insertClient(ClientToSave client);

  @Update("update Client set (name, surname, patronymic, birthDate, gender, charm) = " +
    "(#{name}, #{surname}, #{patronymic}, #{birthDate}, #{gender}, #{charm}) where id=#{id}")
  void updateClient(ClientToSave client);

  @Update("update ClientAddr set (street, house, flat)=(#{street}, #{house}, #{flat}) where client=#{client} and type=#{type}")
  void updateAddress(ClientAddress address);

  @Delete("delete from ClientPhone where client=#{client} and number=#{number}")
  void deletePhone(ClientPhoneNumber number);

  @Update("update ClientPhone set number=#{number} where client=#{client} and number=#{oldNumber}")
  void updatePhone(ClientPhoneNumberToSave number);

  @Update("<script> update Client set actual=#{actual} WHERE id IN " +
    "<foreach item='item' index='index' collection='ids'" +
    " open='(' separator=',' close=')'>" +
    " #{item}" +
    "</foreach>" +
    "</script>")
  int changeClientsActuality(@Param("ids") List<String> ids, @Param("actual") Boolean actual);


}
