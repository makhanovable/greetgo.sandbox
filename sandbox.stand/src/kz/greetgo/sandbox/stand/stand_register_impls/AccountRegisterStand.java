package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.AccountInfoRegister;

import java.util.ArrayList;
import java.util.List;

@Bean
public class AccountRegisterStand implements AccountInfoRegister {

  @Override
  public List<AccountInfo> getAllAccountInfo() {
    ArrayList<AccountInfo> accountInfoList = new ArrayList<>();

    accountInfoList.add(new AccountInfo(1, "First Full Name", "Charm1", 12, 123f, 34f, 22f));
    accountInfoList.add(new AccountInfo(2, "Second Full Name", "Charm2", 12, 123f, 34f, 22f));
    accountInfoList.add(new AccountInfo(3, "Third Full Name", "Charm3", 12, 123f, 34f, 22f));
    accountInfoList.add(new AccountInfo(4, "Fourth Full Name", "Charm4", 12, 123f, 34f, 22f));

    return accountInfoList;
  }

  @Override
  public String healthCheck() {
    return "HEALTH CHECK: OK";
  }
}
