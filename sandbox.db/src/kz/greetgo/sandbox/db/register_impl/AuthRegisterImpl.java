package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.IllegalLoginOrPassword;
import kz.greetgo.sandbox.controller.errors.NoAccountName;
import kz.greetgo.sandbox.controller.errors.NoPassword;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;
import kz.greetgo.sandbox.db.dao.AuthDao;

import java.util.List;

@Bean
public class AuthRegisterImpl implements AuthRegister {

  public BeanGetter<AuthDao> authDao;

  @Override
  public void saveParam(String personId, UserParamName name, String value) {
    authDao.get().saveUserParam(personId, name, value);
  }

  @Override
  public String getParam(String personId, UserParamName name) {
    return authDao.get().getUserParam(personId, name);
  }

  public BeanGetter<TokenRegister> tokenManager;

  @Override
  public String login(String accountName, String password) {

    if (accountName == null || accountName.length() == 0) throw new NoAccountName();
    if (password == null || password.length() == 0) throw new NoPassword();

    String encryptPassword = tokenManager.get().encryptPassword(password);
    if (encryptPassword == null) throw new IllegalLoginOrPassword();

    String personId = authDao.get().selectPersonIdByAccountAndPassword(accountName, encryptPassword);
    if (personId == null) throw new IllegalLoginOrPassword();

    SessionInfo sessionInfo = new SessionInfo(personId);

    return tokenManager.get().createToken(sessionInfo);
  }

  private final ThreadLocal<SessionInfo> sessionInfo = new ThreadLocal<>();

  @Override
  public void checkTokenAndPutToThreadLocal(String token) {
    SessionInfo sessionInfo = tokenManager.get().decryptToken(token);
    this.sessionInfo.set(sessionInfo);
    if (sessionInfo == null) throw new SecurityError();
  }

  @Override
  public void cleanTokenThreadLocal() {
    sessionInfo.set(null);
  }

  @Override
  public SessionInfo getSessionInfo() {
    return sessionInfo.get();
  }

  @Override
  public AuthInfo getAuthInfo(String personId) {
    String accountName = authDao.get().accountNameByPersonId(personId);
    if (accountName == null) throw new NotFound();
    AuthInfo ret = new AuthInfo();
    ret.pageSize = 50;
    ret.appTitle = accountName + " - Sandbox";
    return ret;
  }

  @Override
  public UserInfo getUserInfo(String personId) {
    UserInfo userInfo = authDao.get().getUserInfo(personId);
    if (userInfo == null) throw new NotFound();
    return userInfo;
  }

  @Override
  public List<UserInfo> getAllUserInfo() {
    throw new UnsupportedOperationException("Not Implemented");
  }
}
