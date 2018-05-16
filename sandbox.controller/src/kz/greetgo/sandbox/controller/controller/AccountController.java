package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.AccountInfoRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/account")
public class AccountController implements Controller {

  public BeanGetter<AccountInfoRegister> accInfoRegister;

  @ToJson
  @Mapping("/")
  public List<AccountInfo > getAllAccountInfo() {
    return accInfoRegister.get().getAllAccountInfo();
  }

  @ToJson
  @Mapping("/ok")
  public String healthCheck() {
    return accInfoRegister.get().healthCheck();
  }
}
