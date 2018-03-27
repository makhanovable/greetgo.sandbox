package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sun.misc.Version.print;

//TODO: Для методов, не относящихся к авторизации, сделай отдельный новый регистр. И там реализовывай бизнес логику.
//(Done)

@Bean
public class AuthRegisterStand implements AuthRegister {

  private final Path storageDir = new File("build/user_param_storage").toPath();

  private File getUserParamFile(UserParamName name) {
    return storageDir.resolve(name.name() + ".txt").toFile();
  }

  @Override
  public void saveParam(String personId, UserParamName name, String value) {
    File paramFile = getUserParamFile(name);
    if (value == null) {
      if (paramFile.exists()) paramFile.delete();
      return;
    }
    paramFile.getParentFile().mkdirs();
    try (PrintStream out = new PrintStream(paramFile, "UTF-8")) {
      out.print(value);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getParam(String personId, UserParamName name) {
    File paramFile = getUserParamFile(name);
    if (!paramFile.exists()) return null;
    try {
      return ServerUtil.streamToStr(new FileInputStream(paramFile));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public BeanGetter<StandDb> db;

  @Override
  public String login(String accountName, String password) {
    if (!"111".equals(password)) {
      throw new AuthError("Пароль 111");
    }

    StringBuilder err = new StringBuilder();
    err.append("Use one of: ");

    for (PersonDot personDot : db.get().personStorage.values()) {
      if (!personDot.disabled) err.append(personDot.accountName).append(", ");
      if (accountName == null) continue;
      if (accountName.equals(personDot.accountName)) {
        if (personDot.disabled) throw new AuthError("Account " + accountName + " is disabled");
        return "token:personId=" + personDot.id;
      }
    }

    err.setLength(err.length() - 2);

    throw new AuthError(err.toString());
  }

  private final ThreadLocal<SessionInfo> sessionInfoThreadLocal = new InheritableThreadLocal<>();

  @Override
  public void checkTokenAndPutToThreadLocal(String token) {
    sessionInfoThreadLocal.set(null);
    if (token == null) throw new SecurityError("token is null");
    if (!token.startsWith("token:")) throw new SecurityError("Token does not contain prefix `token:`");
    String personId = null;
    for (String pair : token.substring("token:".length()).split(",")) {
      String[] split = pair.split("=");
      if (split.length != 2) throw new SecurityError("Left token = [[" + token + "]]");
      if (split[0].equals("personId")) {
        personId = split[1];
        continue;
      }
      throw new SecurityError("Unknown token field [[" + split[0] + "]]. Token = [[" + token + "]]");
    }

    if (personId == null) throw new SecurityError("No personId in token");
    sessionInfoThreadLocal.set(new SessionInfo(personId));
  }

  @Override
  public void cleanTokenThreadLocal() {
    sessionInfoThreadLocal.set(null);
  }

  @Override
  public SessionInfo getSessionInfo() {
    return sessionInfoThreadLocal.get();
  }

  @Override
  public AuthInfo getAuthInfo(String personId) {
    PersonDot personDot = db.get().personStorage.get(personId);
    if (personDot == null) throw new NullPointerException("personDot == null for id = " + personId);
    AuthInfo ret = new AuthInfo();
    ret.pageSize = 7;
    ret.appTitle = personDot.accountName + " - Sandbox STAND";
    return ret;
  }

  @Override
  public UserInfo getUserInfo(String personId) {
        for (PersonDot person : db.get().personStorage.values()) {
            System.out.println(person.accountName);
        }

        return db.get().personStorage.get(personId).toUserInfo();
    }

    public List<UserInfo> getAllUserInfo() {

        List<UserInfo> users = new ArrayList<UserInfo>();
        for (PersonDot person : db.get().personStorage.values()) {
            users.add(person.toUserInfo());
        }

        return users;
    }

}
