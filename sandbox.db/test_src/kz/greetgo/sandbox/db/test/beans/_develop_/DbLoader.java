package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.TokenRegister;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import org.apache.log4j.Logger;

import java.util.function.Function;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;
  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
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
      .peek(CharmDot::showInfo)
      .forEach(charmTestDao.get()::insertCharmDot);

    logger.info("Finish loading test data");
  }
}
