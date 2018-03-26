package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.test.model.AccountFrs;
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

  @Insert("insert into ${tableName} (id, client_id, account_number, registeredAt, error) " +
    "values (#{acc.id}, #{acc.client_id}, #{acc.account_number}, #{acc.registered_at}, #{acc.error})")
  void insertIntoTempAccount(@Param("acc") AccountFrs account, @Param("tableName") String tableName);

  @Insert("insert into ${tableName} (id, account_number, money, finished_at, type, error) " +
    "values (#{tra.id}, #{tra.account_number}, #{tra.money}, #{tra.finished_at}, #{tra.transaction_type}, #{tra.error} )")
  void insertIntoTempTransaction(@Param("tra") Transaction transaction, @Param("tableName") String tableName);

  @Select("select id, client_id, account_number, registeredAt as registered_at, error from ${tableName}")
  List<AccountFrs> getTempAccountList(@Param("tableName") String tableName);


  @Select("select id, client as client_id, number as account_number, to_char(registeredAt, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as registered_at from clientAccount\n" +
    "order by number")
  List<AccountFrs> getRealAccountListOrderByNumber();


  @Select("select id, finished_at, type as transaction_type, account_number, error from ${tableName}")
  List<Transaction> getTempTransactionList(@Param("tableName") String tableName);

  @Select("select cat.id, to_char(finishedat, 'YYYY-MM-dd\"T\"HH24:MI:SS.MS') as finished_at, tt.name as transaction_type, account as account_number from clientaccounttransaction cat\n" +
    "left join transactiontype tt on tt.id=cat.type order by id")
  List<Transaction> getRealTransactionListOrderById();


  @Select("select ${column} from ${tableName}")
  List<String> getList(@Param("tableName") String tableName, @Param("column") String column);

  @Select("TRUNCATE clientaccount cascade; TRUNCATE clientaccounttransaction cascade")
  void clear();
}
