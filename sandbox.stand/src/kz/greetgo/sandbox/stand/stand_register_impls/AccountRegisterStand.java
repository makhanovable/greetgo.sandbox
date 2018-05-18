package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Account;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AccountDot;

import java.time.Instant;
import java.util.*;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

@Bean
public class AccountRegisterStand implements AccountRegister {

//  private final Path storageDir = new File()

  public BeanGetter<StandDb> db;

  @Override
  public List<AccountInfo> getAllAccountInfo() {
    ArrayList<AccountInfo> accountInfoList = new ArrayList<>();

    for(ClientDot clientDot : db.get().clientStorage.values()) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.id = clientDot.id;
        accountInfo.fullName = String.format("%s %s %s", clientDot.name, clientDot.surname, clientDot.patronymic);
        accountInfo.charm = getCharmById(clientDot.charmId);
        accountInfo.age = calculateYearDiff(clientDot.birthDate);

        ArrayList<Account> accounts = selectAccountsByClientId(accountInfo.id);
        if(accounts.size() == 0) continue; // TODO: throw an Exception

        accountInfo.totalAccBalance = getTotalAccBalance(accounts);
        accountInfo.minAccBalance = getMinAccBalance(accounts);
        accountInfo.maxAccBalance = getMaxAccBalance(accounts);

      accountInfoList.add(accountInfo);
    }

    return accountInfoList;
  }

  private float getMinAccBalance(ArrayList<Account> accounts) {
    float result = accounts.get(0).money;
    for(int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if(curAcc.money < result) result = curAcc.money;
    }
    return result;
  }

  private float getMaxAccBalance(ArrayList<Account> accounts) {
    float result = accounts.get(0).money;
    for(int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if(curAcc.money > result) result = curAcc.money;
    }
    return result;
  }


  private float getTotalAccBalance(ArrayList<Account> accounts) {
    float result = 0f;
    for(Account acc : accounts) {
      result += acc.money;
    }
    return result;
  }

  private ArrayList<Account> selectAccountsByClientId(int clientId) {
    ArrayList<Account> accounts = new ArrayList<>();

    for(AccountDot accountDot: db.get().accountStorage.values()) {
      if(accountDot.clientId == clientId) accounts.add(accountDot.toAccount());
    }

    return accounts;
  }

  private String getCharmById(int charmId) {
    return db.get().charmStorage.get(charmId).name;
  }

  private int calculateYearDiff(Date date) {
    Calendar dateNow = Calendar.getInstance();
    dateNow.setTime(Date.from(Instant.now()));

    Calendar dateBirth = Calendar.getInstance();
    dateBirth.setTime(date);

    int diff = dateNow.get(YEAR) - dateBirth.get(YEAR);
    if (dateNow.get(MONTH) > dateBirth.get(MONTH) ||
      (dateNow.get(MONTH) == dateBirth.get(MONTH) && dateNow.get(DATE) > dateBirth.get(DATE))) {
      diff--;
    }
    return diff;
  }

}
