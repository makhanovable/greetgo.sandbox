package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.IllegalLoginOrPassword;
import kz.greetgo.sandbox.controller.errors.NoAccountName;
import kz.greetgo.sandbox.controller.errors.NoPassword;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.auth.AuthRegister;
import kz.greetgo.sandbox.controller.register.auth.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.auth.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;
import kz.greetgo.sandbox.db.errors.RedPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.CheckPoliceResponse;
import kz.greetgo.sandbox.db.in_service.model.PoliceStatus;
import kz.greetgo.sandbox.db.test.beans.PoliceCheckServiceForTests;
import kz.greetgo.sandbox.db.test.dao.AuthTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Набор автоматизированных тестов для тестирования методов класса {@link AuthRegisterImpl}
 */
public class AuthRegisterImplTest extends ParentTestNg {

  public BeanGetter<AuthRegister> authRegister;

  public BeanGetter<AuthTestDao> authTestDao;

  public BeanGetter<PoliceCheckServiceForTests> policeCheckService;

  @BeforeMethod
  public void cleanPoliceCheckService() throws Exception {
    policeCheckService.get().clean();
  }

  @DataProvider
  public Object[][] saveParam_DP() {
    return Arrays.stream(UserParamName.values())
      .map(a -> new Object[]{a})
      .toArray(Object[][]::new);
  }

  @Test(dataProvider = "saveParam_DP")
  public void saveParam_notNull_insert(UserParamName paramName) throws Exception {
    String personId = RND.str(10);
    String expectedValue = RND.str(10);

    //Clear
    //add test data
    //register call
    //assert

    //
    //
    authRegister.get().saveParam(personId, paramName, expectedValue);
    //
    //

    {
      int count = authTestDao.get().countOfUserParams(personId, paramName);
      assertThat(count).isEqualTo(1);
    }

    {
      String actualValue = authTestDao.get().loadParamValue(personId, paramName);
      assertThat(actualValue).isEqualTo(expectedValue);
    }
  }

  @Test(dataProvider = "saveParam_DP")
  public void saveParam_notNull_update(UserParamName paramName) throws Exception {
    String personId = RND.str(10);
    String expectedValue = RND.str(10);

    authTestDao.get().insertUserParam(personId, paramName, RND.str(10));

    //
    //
    authRegister.get().saveParam(personId, paramName, expectedValue);
    //
    //

    {
      int count = authTestDao.get().countOfUserParams(personId, paramName);
      assertThat(count).isEqualTo(1);
    }

    {
      String actualValue = authTestDao.get().loadParamValue(personId, paramName);
      assertThat(actualValue).isEqualTo(expectedValue);
    }
  }

  @Test(dataProvider = "saveParam_DP")
  public void saveParam_null(UserParamName paramName) throws Exception {
    String personId = RND.str(10);

    authTestDao.get().insertUserParam(personId, paramName, RND.str(10));

    {
      int count = authTestDao.get().countOfUserParams(personId, paramName);
      assertThat(count).isEqualTo(1);
    }

    //
    //
    authRegister.get().saveParam(personId, paramName, null);
    //
    //

    {
      int count = authTestDao.get().countOfUserParams(personId, paramName);
      assertThat(count).isZero();
    }
  }

  @Test(dataProvider = "saveParam_DP")
  public void getParam_noRecord(UserParamName paramName) throws Exception {

    //
    //
    String value = authRegister.get().getParam(RND.str(10), paramName);
    //
    //

    assertThat(value).isNull();
  }

  @Test(dataProvider = "saveParam_DP")
  public void getParam_hasRecordButNull(UserParamName paramName) throws Exception {

    String personId = RND.str(10);

    authTestDao.get().insertUserParam(personId, paramName, null);

    //
    //
    String value = authRegister.get().getParam(personId, paramName);
    //
    //

    assertThat(value).isNull();
  }

  @Test(dataProvider = "saveParam_DP")
  public void getParam_value(UserParamName paramName) throws Exception {

    String personId = RND.str(10);
    String expectedValue = RND.str(10);

    authTestDao.get().insertUserParam(personId, paramName, expectedValue);

    //
    //
    String value = authRegister.get().getParam(personId, paramName);
    //
    //

    assertThat(value).isEqualTo(expectedValue);
  }

  public BeanGetter<TokenRegister> tokenManager;

  @Test
  public void login_ok() throws Exception {
    String password = RND.str(10);
    String encryptedPassword = tokenManager.get().encryptPassword(password);

    String accountName = RND.str(10);
    String id = RND.str(10);

    authTestDao.get().insertUser(id, accountName, encryptedPassword, 0);

    //
    //
    String token = authRegister.get().login(accountName, password);
    //
    //

    assertThat(token).isNotNull();
    SessionInfo sessionInfo = tokenManager.get().decryptToken(token);
    assertThat(sessionInfo).isNotNull();
    assertThat(sessionInfo.personId).isEqualTo(id);
  }

  @Test(expectedExceptions = IllegalLoginOrPassword.class)
  public void login_blocked() throws Exception {
    String password = RND.str(10);
    String encryptPassword = tokenManager.get().encryptPassword(password);

    String accountName = RND.str(10);
    String id = RND.str(10);

    authTestDao.get().insertUser(id, accountName, encryptPassword, 1);

    //
    //
    authRegister.get().login(accountName, password);
    //
    //
  }

  @Test(expectedExceptions = IllegalLoginOrPassword.class)
  public void login_leftPassword() throws Exception {
    String password = RND.str(10);
    String encryptedPassword = tokenManager.get().encryptPassword(password);

    String accountName = RND.str(10);
    String id = RND.str(10);

    authTestDao.get().insertUser(id, accountName, encryptedPassword, 0);

    //
    //
    authRegister.get().login(accountName, RND.str(10));
    //
    //
  }

  @Test(expectedExceptions = IllegalLoginOrPassword.class)
  public void login_noPerson() throws Exception {
    authRegister.get().login(RND.str(10), RND.str(10));
  }


  @Test(expectedExceptions = NoPassword.class)
  public void login_passwordIsEmpty() throws Exception {
    authRegister.get().login(RND.str(10), "");
  }

  @Test(expectedExceptions = NoPassword.class)
  public void login_passwordIsNull() throws Exception {
    authRegister.get().login(RND.str(10), null);
  }

  @Test(expectedExceptions = NoAccountName.class)
  public void login_accountNameIsNull() throws Exception {
    authRegister.get().login(null, RND.str(10));
  }

  @Test(expectedExceptions = NoAccountName.class)
  public void login_accountNameIsEmpty() throws Exception {
    authRegister.get().login("", RND.str(10));
  }

  @Test
  public void checkTokenAndPutToThreadLocal_ok() throws Exception {

    final int threadCount = 40;

    class Context implements Runnable {
      final Thread thread = new Thread(this);
      private int threadIndex;

      public Context(int threadIndex) {
        this.threadIndex = threadIndex;
      }

      public void start() {
        thread.start();
      }

      final SessionInfo sessionInfo = new SessionInfo(RND.str(10));
      SessionInfo sessionInfo_back;
      SessionInfo sessionInfo_back2;

      Throwable error = null;

      @Override
      public void run() {
        try {
          String token = tokenManager.get().createToken(sessionInfo);

          Thread.sleep(RND.plusInt(30) + 1);

          authRegister.get().checkTokenAndPutToThreadLocal(token);

          Thread.sleep(RND.plusInt(30) + 1);

          sessionInfo_back = authRegister.get().getSessionInfo();

          Thread.sleep(RND.plusInt(30) + 1);

          authRegister.get().cleanTokenThreadLocal();

          Thread.sleep(RND.plusInt(30) + 1);

          sessionInfo_back2 = authRegister.get().getSessionInfo();

        } catch (Throwable e) {
          error = e;
          new RuntimeException("Error in thread index = " + threadIndex, e).printStackTrace();
        }
      }

      public void join() {
        try {
          thread.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      public void asserts() {
        String d = "threadIndex = " + threadIndex;
        assertThat(error).describedAs(d).isNull();

        assertThat(sessionInfo_back).describedAs(d).isNotNull();
        assertThat(sessionInfo_back.personId).describedAs(d).isEqualTo(sessionInfo.personId);
        assertThat(sessionInfo_back2).describedAs(d).isNull();
      }
    }

    List<Context> list = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      list.add(new Context(i));
    }

    list.forEach(Context::start);
    list.forEach(Context::join);
    list.forEach(Context::asserts);
  }

  @Test(expectedExceptions = SecurityError.class)
  public void checkTokenAndPutToThreadLocal_SecurityError_onLeftToken() throws Exception {
    authRegister.get().checkTokenAndPutToThreadLocal("fds2fd2sf2dsf");
  }

  @Test(expectedExceptions = SecurityError.class)
  public void checkTokenAndPutToThreadLocal_SecurityError_onNull() throws Exception {
    authRegister.get().checkTokenAndPutToThreadLocal(null);
  }

  @Test
  public void sessionInfoCleanedIfTokenIsLeft() throws Exception {
    final SessionInfo sessionInfo = new SessionInfo(RND.str(10));
    String token = tokenManager.get().createToken(sessionInfo);
    authRegister.get().checkTokenAndPutToThreadLocal(token);

    assertThat(authRegister.get().getSessionInfo()).isNotNull();

    SecurityError error = null;

    try {
      authRegister.get().checkTokenAndPutToThreadLocal("fds4fa4dsf");
    } catch (SecurityError e) {
      error = e;
    }

    assertThat(error).isNotNull();

    assertThat(authRegister.get().getSessionInfo()).isNull();
  }

  @Test
  public void getAuthInfo_ok() throws Exception {
    String accountName = RND.str(10);
    String id = RND.str(10);
    authTestDao.get().insertUser(id, accountName, "asd", 1);

    //
    //
    AuthInfo userInfo = authRegister.get().getAuthInfo(id);
    //
    //

    assertThat(userInfo).isNotNull();
    assertThat(userInfo.pageSize).isEqualTo(50);
    assertThat(userInfo.appTitle).isEqualTo(accountName + " - Sandbox");
  }

  @Test(expectedExceptions = NotFound.class)
  public void getAuthInfo_NotFound() throws Exception {
    authRegister.get().getAuthInfo(RND.str(10));
  }

  @Test(expectedExceptions = NotFound.class)
  public void getUserInfo_NotFound() throws Exception {
    authRegister.get().getUserInfo(RND.str(10));
  }

  @Test
  public void getUserInfo_ok() throws Exception {
    String accountName = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);
    String patronymic = RND.str(10);
    String id = RND.str(10);
    authTestDao.get().insertUser(id, accountName, "asd", 1);
    authTestDao.get().updatePersonField(id, "surname", surname);
    authTestDao.get().updatePersonField(id, "name", name);
    authTestDao.get().updatePersonField(id, "patronymic", patronymic);

    policeCheckService.get().checkNaturalPerson_out.add(new CheckPoliceResponse(PoliceStatus.GREEN));

    //
    //
    UserInfo userInfo = authRegister.get().getUserInfo(id);
    //
    //

    assertThat(userInfo).isNotNull();
    assertThat(userInfo.id).isEqualTo(id);
    assertThat(userInfo.accountName).isEqualTo(accountName);
    assertThat(userInfo.surname).isEqualTo(surname);
    assertThat(userInfo.name).isEqualTo(name);
    assertThat(userInfo.patronymic).isEqualTo(patronymic);
  }


  @Test
  public void getUserInfo_ok_checkPoliceGreen() throws Exception {
    String accountName = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);
    String patronymic = RND.str(10);
    String id = RND.str(10);
    authTestDao.get().insertUser(id, accountName, "asd", 1);
    authTestDao.get().updatePersonField(id, "surname", surname);
    authTestDao.get().updatePersonField(id, "name", name);
    authTestDao.get().updatePersonField(id, "patronymic", patronymic);

    policeCheckService.get().checkNaturalPerson_out.add(new CheckPoliceResponse(PoliceStatus.GREEN));

    //
    //
    UserInfo userInfo = authRegister.get().getUserInfo(id);
    //
    //

    assertThat(userInfo).isNotNull();
    assertThat(userInfo.id).isEqualTo(id);
    assertThat(userInfo.accountName).isEqualTo(accountName);
    assertThat(userInfo.surname).isEqualTo(surname);
    assertThat(userInfo.name).isEqualTo(name);
    assertThat(userInfo.patronymic).isEqualTo(patronymic);

    assertThat(userInfo.yellow).isFalse();

    assertThat(policeCheckService.get().checkNaturalPerson_input).hasSize(1);
    assertThat(policeCheckService.get().checkNaturalPerson_input.get(0).surname).isEqualTo(surname);
    assertThat(policeCheckService.get().checkNaturalPerson_input.get(0).name).isEqualTo(name);
    assertThat(policeCheckService.get().checkNaturalPerson_input.get(0).patronymic).isEqualTo(patronymic);
  }

  @Test
  public void getUserInfo_ok_checkPoliceYellow() throws Exception {
    String accountName = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);
    String patronymic = RND.str(10);
    String id = RND.str(10);
    authTestDao.get().insertUser(id, accountName, "asd", 1);
    authTestDao.get().updatePersonField(id, "surname", surname);
    authTestDao.get().updatePersonField(id, "name", name);
    authTestDao.get().updatePersonField(id, "patronymic", patronymic);

    policeCheckService.get().checkNaturalPerson_out.add(new CheckPoliceResponse(PoliceStatus.YELLOW));

    //
    //
    UserInfo userInfo = authRegister.get().getUserInfo(id);
    //
    //

    assertThat(userInfo.yellow).isTrue();
  }

  @Test(expectedExceptions = RedPoliceResponse.class)
  public void getUserInfo_ok_checkPoliceRed() throws Exception {
    String accountName = RND.str(10);
    String surname = RND.str(10);
    String name = RND.str(10);
    String patronymic = RND.str(10);
    String id = RND.str(10);
    authTestDao.get().insertUser(id, accountName, "asd", 1);
    authTestDao.get().updatePersonField(id, "surname", surname);
    authTestDao.get().updatePersonField(id, "name", name);
    authTestDao.get().updatePersonField(id, "patronymic", patronymic);

    policeCheckService.get().checkNaturalPerson_out.add(new CheckPoliceResponse(PoliceStatus.RED));

    //
    //
    authRegister.get().getUserInfo(id);
    //
    //
  }


}
