package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.TransactionType;
import org.apache.ibatis.annotations.Insert;

public interface TransactionTypeTestDao {

    @Insert("insert into transaction_types (id, code, name) " +
            "values (#{id}, #{code}, #{name})")
    void insertTransactionType(TransactionType transactionType);
}
