package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.test.model.Account;
import kz.greetgo.sandbox.db.test.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Bean
public interface AccountTestDao {

  @Insert("insert into ClientAccount (id, client, money, number, registeredAt) " +
    "values (#{id}, #{client}, #{money}, #{number}, #{registeredAt})")
  void insertAccount(ClientAccountDot accountDot);

  @Select("select client_id, account_number, registeredAt as registered_at, error from ${tableName}")
  List<Account> getAccountList(@Param("tableName") String tableName);

  @Select("select finished_at, type as transaction_type, account_number, error from ${tableName}")
  List<Transaction> getTransactionList(@Param("tableName") String tableName);

  @Select(" create table ${tableName} (\n" +
    "        no bigserial,\n" +
    "        id varchar(32),\n" +
    "        client_id varchar(32),\n" +
    "        money varchar(100),\n" +
    "        account_number varchar(100),\n" +
    "        registeredAt varchar(100),\n" +
    "        error varchar(100),\n" +
    "        mig_status smallint default 1,\n" +
    "        PRIMARY KEY(no)\n" +
    "      );")
  void createTempAccountTable(@Param("tableName") String tableName);

  @Select("create table ${tableName} (\n" +
    "  no bigserial,\n" +
    "  id varchar(35),\n" +
    "  account_number varchar(35),\n" +
    "  money varchar(100),\n" +
    "  finished_at varchar(100),\n" +
    "  type varchar(100),\n" +
    "  error varchar(100),\n" +
    "  mig_status smallint default 1,\n" +
    "  PRIMARY KEY (no)\n" +
    ")")
  void createTempTransactionTable(@Param("tableName") String tableName);


}
