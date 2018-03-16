package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.PrintedClientInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

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

  @ToJson
  @Mapping("/allUserInfo")
  public List<UserInfo> allUserInfo() {
    return authRegister.get().getAllUserInfo();
  }

  @ToJson
  @Mapping("/clientsInfo")
  public List<PrintedClientInfo> clientsInfo() {
    return authRegister.get().getClientsInfo();
  }

  //TODO: перенеси все методы контроллера, не относящиеся по логике к авторизации, в новый контроллер для клиентов.
  //TODO: также создай новый регистр для клиентов
  
  @AsIs
  @NoSecurity
  @Mapping("/addNewClient")
  public String addNewClient(@Par("clientInfo") String clientInfo) {
        return authRegister.get().addNewClient(clientInfo);
  }

  @AsIs
  @NoSecurity
  @Mapping("/addNewPhone")
  public String addNewPhone(@Par("phones") String phones) {
    return authRegister.get().addNewPhone(phones);
  }

  @AsIs
  @NoSecurity
  @Mapping("/addNewAdress")
  public String addNewAdresses(@Par("adresses") String adresses) {
    return authRegister.get().addNewAdresses(adresses);
  }
}
