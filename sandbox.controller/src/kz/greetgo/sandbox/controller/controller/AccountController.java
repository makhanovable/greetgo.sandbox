package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientAccountInfoPage;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/accounts")
public class AccountController implements Controller {

  public BeanGetter<AccountRegister> accInfoRegister;

  @ToJson
  @Mapping("/")
  public ClientAccountInfoPage getAllAccountInfo(@Json @Par("requestDetails") TableRequestDetails requestDetails) {
    return accInfoRegister.get().getAllAccountInfo(requestDetails);
  }

}
