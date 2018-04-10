package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.TokenRegister;
import kz.greetgo.sandbox.db.test.dao.*;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import org.apache.log4j.Logger;

import java.util.function.Function;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;
  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<AdressTestDao> adressTestDao;
  public BeanGetter<PhoneTestDao> phoneTestDao;
  public BeanGetter<AccountTestDao> accountTestDao;
  public BeanGetter<TransactionTypeTestDao> transactionTypeTestDao;
  public BeanGetter<TransactionTestDao> transactionTestDao;
  public BeanGetter<TokenRegister> tokenManager;

  public void loadTestData() {
    logger.info("Start loading test data...");

    logger.info("Loading persons...");
    Function<String, String> passwordEncryption = tokenManager.get()::encryptPassword;
    standDb.get().personStorage.values().stream()
      .peek(p -> p.encryptedPassword = passwordEncryption.apply(p.password))
      .peek(PersonDot::showInfo)
      .forEach(authTestDao.get()::insertPersonDot);

    logger.info("Loading charms...");
    standDb.get().charmStorage.values().stream()
            .forEach(charmTestDao.get()::insertCharm);

    logger.info("Loading clients...");
    standDb.get().clientStorage.values().stream()
            .forEach(clientTestDao.get()::insertClient);

    logger.info("Loading adresses...");
    standDb.get().adressStorage.values().stream()
            .forEach(adressTestDao.get()::insertAdress);

    logger.info("Loading phones...");
    standDb.get().phoneStorage.values().stream()
            .forEach(phoneTestDao.get()::insertPhone);

    logger.info("Loading accounts...");
    standDb.get().accountStorage.values().stream()
            .forEach(accountTestDao.get()::insertAccount);

    logger.info("Loading transaction types...");
    standDb.get().transactionTypeStorage.values().stream()
            .forEach(transactionTypeTestDao.get()::insertTransactionType);

    logger.info("Loading transactions...");
    standDb.get().transactionStorage.values().stream()
            .forEach(transactionTestDao.get()::insertTransaction);

    logger.info("Finish loading test data");
  }
}
