package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.auth.AuthRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/auth")
public class AuthController implements Controller {

  public BeanGetter<AuthRegister> authRegister;

  @AsIs
  @NoSecurity
  @Mapping("/login")
  public String login(@Par("accountName") String accountName, @Par("password") String password) {
    return authRegister.get().login(accountName, password);
  }

  @ToJson
  @Mapping("/info")
  public AuthInfo info(@ParSession("personId") String personId) {
    return authRegister.get().getAuthInfo(personId);
  }

  @ToJson
  @Mapping("/userInfo")
  public UserInfo userInfo(@ParSession("personId") String personId) {
    return authRegister.get().getUserInfo(personId);
  }

}
