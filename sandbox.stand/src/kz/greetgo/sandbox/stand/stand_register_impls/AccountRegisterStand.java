package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfo;
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
      accountInfoList.add(acc.toAccountInfo());
    }

    return accountInfoList;
  }

  @Override
  public ClientInfo getClientInfo(int clientId) {
//    System.out.println(clientId);

    return null;
  }

  @Override
  public String healthCheck() {
    return "HEALTH CHECK: OK";
  }
}
