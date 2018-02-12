package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientDao {

  @Select("select count(1) from Client c where c.name")
  int countByFilter(@Param("filter") String filter);

  @Select("select sount(1) from Client")
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
  ClientAddress getAddresses(@Param("client") String client, @Param("type") AddressType type);

  @Insert("insert into ClientPhone (client, number, type) " +
    "values (#{client}, #{number}, #{type})")
  void insertPhone(ClientPhoneNumber phone);

  @Insert("insert into ClientAddr (client, type, street, house, flat) " +
    "values (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(ClientPhoneNumber phone);


}
