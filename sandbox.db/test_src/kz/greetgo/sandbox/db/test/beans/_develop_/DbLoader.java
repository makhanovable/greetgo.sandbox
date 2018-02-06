package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressInfo;
import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.register_impl.TokenRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.util.RND;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.function.Function;

@Bean
public class DbLoader {
  final Logger logger = Logger.getLogger(getClass());

  public BeanGetter<StandDb> standDb;

  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<ClientTestDao> clientTestDao;

  public BeanGetter<TokenRegister> tokenManager;

  public void loadTestData() {
    logger.info("Start loading test data...");

    logger.info("Loading persons...");
    Function<String, String> passwordEncryption = tokenManager.get()::encryptPassword;
    standDb.get().personStorage.values().stream()
      .peek(p -> p.encryptedPassword = passwordEncryption.apply(p.password))
      .peek(PersonDot::showInfo)
      .forEach(authTestDao.get()::insertPersonDot);

    standDb.get().charmStorage.values().forEach(charmDot -> {
      clientTestDao.get().insertCharm(charmDot.id, charmDot.name, RND.str(10), (float) RND.plusDouble(100, Util.decimalNum));
      System.out.println(charmDot);
    });

    standDb.get().clientStorage.values().forEach(clientDot -> {
      long id = clientTestDao.get().selectSeqIdNextValueTableClient();

      clientTestDao.get().insertClient(id, clientDot.surname, clientDot.name, clientDot.patronymic,
        clientDot.gender.name(), Date.valueOf(clientDot.birthDate), clientDot.charm.id);

      AddressInfo ai = clientDot.factualAddressInfo;
      clientTestDao.get().insertClientAddr(id, ai.type.name(), ai.street, ai.house, ai.flat);

      ai = clientDot.registrationAddressInfo;
      clientTestDao.get().insertClientAddr(id, ai.type.name(), ai.street, ai.house, ai.flat);

      for (Phone phone : clientDot.phones)
        clientTestDao.get().insertClientPhone(id, phone.number, phone.type.name());

      for (int i = 0; i < RND.plusInt(5); i++) {
        clientTestDao.get().insertClientAccount(clientTestDao.get().selectSeqIdNextValueTableClientAccount(),
          id, (float) (RND.plusDouble(500000, Util.decimalNum) - 1000000), RND.str(10), new Timestamp(Util.generateDate().getTime()));
      }
    });


    logger.info("Finish loading test data");
  }
}
