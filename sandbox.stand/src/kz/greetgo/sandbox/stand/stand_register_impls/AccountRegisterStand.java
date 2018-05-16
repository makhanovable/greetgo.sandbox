package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.AccountInfoRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AccountInfoDot;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Bean
public class AccountRegisterStand implements AccountInfoRegister {

//  private final Path storageDir = new File()

  public BeanGetter<StandDb> db;

  @Override
  public List<AccountInfo> getAllAccountInfo() {
    ArrayList<AccountInfo> accountInfoList = new ArrayList<>();

    for(AccountInfoDot acc : db.get().accountInfoStorage.values()) {
//      System.out.println(acc.fullName + acc.age);
      accountInfoList.add(acc.toAccountInfo());
    }

//    accountInfoList.add(new AccountInfo(1, "First Full Name", "Charm1", 12, 123f, 34f, 22f));
//    accountInfoList.add(new AccountInfo(2, "Second Full Name", "Charm2", 12, 123f, 34f, 22f));
//    accountInfoList.add(new AccountInfo(3, "Third Full Name", "Charm3", 12, 123f, 34f, 22f));
//    accountInfoList.add(new AccountInfo(4, "Fourth Full Name", "Charm4", 12, 123f, 34f, 22f));

    return accountInfoList;
  }

  @Override
  public String healthCheck() {
    return "HEALTH CHECK: OK";
  }
}
