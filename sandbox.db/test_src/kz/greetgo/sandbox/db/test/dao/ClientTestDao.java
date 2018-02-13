package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

  @Select("select id, name, surname, patronymic, birthDate, gender, charm from client where id=#{id}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "phoneNumbers", column = "id", javaType = List.class,
      many = @Many(select = "getNumbersById"))
  })
  ClientDetail detail(@Param("id") String id);

  @Select("select client, number, type from ClientPhone where client=#{client} and actual=true")
  List<ClientPhoneNumber> getNumbersById(String client);

  @Select("select client, type, street, house, flat from ClientAddr where client=#{client} and type=#{type}")
  ClientAddress getAddres(@Param("client") String client, @Param("type") AddressType type);


  @Select("TRUNCATE Client; TRUNCATE ClientPhone; TRUNCATE ClientAddr")
  void clear();
}
