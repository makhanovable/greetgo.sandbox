package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.TransactionDot;
import org.apache.ibatis.annotations.Insert;

public interface TransactionTestDao {

    @Insert("insert into transactions (id, account_id, money, finished_at, transaction_type_id) " +
            "values (#{id}, #{accountID}, #{money}, #{finished_at}, #{transactionTypeID})")
    void insertTransaction(TransactionDot transactionDot);
}
