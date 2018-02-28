package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumberToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientDao {

  @Select("select id, name, surname, patronymic, birthDate, gender, charm from client where id=#{id} and actual=true")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "phoneNumbers", column = "id", javaType = List.class,
      many = @Many(select = "getNumbersById"))
  })
  ClientDetail detail(@Param("id") String id);

  @Select("select client, type, street, house, flat from ClientAddr where client=#{client}")
  List<ClientAddress> getAddresses(@Param("client") String client);

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
    "<foreach item='item' collection='ids'" +
    " open='(' separator=',' close=')'>" +
    " #{item}" +
    "</foreach>" +
    "</script>")
  int changeClientsActuality(@Param("ids") List<String> ids, @Param("actual") Boolean actual);
  
}
