package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.AccountInfoRegister;

import java.util.List;

@Bean
public class AccountRegisterStand implements AccountInfoRegister {

  @Override
  public List<AccountInfo> getAllAccountInfo() {

    throw new UnsupportedOperationException();
  }

  @Override
  public String healthCheck() {
    return "HEALTH CHECK: OK";
  }
}
