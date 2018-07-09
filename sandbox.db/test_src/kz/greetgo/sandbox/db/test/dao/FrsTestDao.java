package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration.model.Account;
import kz.greetgo.sandbox.db.migration.model.Transaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FrsTestDao {

    @Select("select * from tmp_acc where cia_client_id = #{cia_id}")
    Account getAccountById(@Param("cia_id") String cia_id);

    @Select("select * from tmp_trans")
    List<Transaction> getTransactions();

}
