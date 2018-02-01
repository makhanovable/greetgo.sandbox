package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.register.TempSessionRegister;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.dao.TempSessionTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TempSessionRegisterImplTest extends ParentTestNg {

  public BeanGetter<AuthTestDao> authTestDao;
  public BeanGetter<TempSessionTestDao> tempSessionTestDao;
  public BeanGetter<TempSessionRegister> tempSessionRegister;

  @Test
  public void method_save_exists() {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);
    String expectedToken = tempSessionRegister.get().save(id, "/test", 60);

    assertThat(true).isEqualTo(tempSessionTestDao.get().selectTokenExists(expectedToken));
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_nonexistentPerson() {
    this.resetTables();

    authTestDao.get().insertUser("id0", "account", "1234", 0);

    tempSessionRegister.get().save("azaza", "/test", 60);
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_invalidToken() {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);
    String token = tempSessionRegister.get().save(id, "/test", 60);

    tempSessionRegister.get().checkForValidity(token + RND.str(3), "/test");
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_invalidUrl() {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);
    String token = tempSessionRegister.get().save(id, "/test/report", 60);

    tempSessionRegister.get().checkForValidity(token, "/important/report");
  }

  @Test(expectedExceptions = AuthError.class)
  public void method_checkSessionForValidity_expiredLifetime() {
    this.resetTables();

    String id = RND.str(10);
    authTestDao.get().insertUser(id, "account", "1234", 0);
    String token = tempSessionRegister.get().save(id, "/test/report", -100);

    tempSessionRegister.get().checkForValidity(token, "/important/report");
  }

  private void resetTables() {
    authTestDao.get().deleteAllTablePerson();
    tempSessionTestDao.get().deleteAll();
  }
}
