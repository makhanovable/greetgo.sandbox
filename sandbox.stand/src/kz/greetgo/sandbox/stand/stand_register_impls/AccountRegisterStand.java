package kz.greetgo.sandbox.stand.stand_register_impls;

import com.google.common.collect.Lists;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;

@Bean
public class AccountRegisterStand implements AccountRegister {

//  private final Path storageDir = new File()

  public BeanGetter<StandDb> db;

  @Override
  public AccountInfoPage getAllAccountInfo(TableRequestDetails requestDetails) {
    ArrayList<AccountInfo> accountInfoList = new ArrayList<>();


    for(ClientDot clientDot : db.get().clientStorage.values()) {
      if(clientDot.isActive) {
        AccountInfo accountInfo = getAccountInfo(clientDot.id);
        accountInfoList.add(accountInfo);
      }
    }

    // Filtration
    accountInfoList = accountInfoList.stream()
      .filter(a -> a.fullName.replaceAll("\\s+", "").toLowerCase()
        .contains(requestDetails.filter.replaceAll("\\s+", "").toLowerCase())
      ).collect(Collectors.toCollection(ArrayList::new));

    // Sorting
    if (requestDetails.sortBy == SortColumn.FIO) {
      accountInfoList.sort((AccountInfo a1, AccountInfo a2) -> a1.fullName.compareTo(a2.fullName));
    } else if (requestDetails.sortBy == SortColumn.AGE) {
      accountInfoList.sort((AccountInfo a1, AccountInfo a2) -> a1.age - a2.age);
    } else if (requestDetails.sortBy == SortColumn.TOTAL) {
      accountInfoList.sort((AccountInfo a1, AccountInfo a2) -> (int)(a1.totalAccBalance - a2.totalAccBalance));
    } else if (requestDetails.sortBy == SortColumn.MIN) {
      accountInfoList.sort((AccountInfo a1, AccountInfo a2) -> (int)(a1.minAccBalance - a2.minAccBalance));
    } else if (requestDetails.sortBy == SortColumn.MAX) {
      accountInfoList.sort((AccountInfo a1, AccountInfo a2) -> (int)(a1.maxAccBalance - a2.maxAccBalance));
    }

    if (requestDetails.sortDirection == SortDirection.DESC) {
      Collections.reverse(accountInfoList);
    }

    // Paginagtion
    int fromIndex = requestDetails.pageIndex * requestDetails.pageSize;
    int toIndex = fromIndex + requestDetails.pageSize;
    if(toIndex > accountInfoList.size()) toIndex = accountInfoList.size();

    return new AccountInfoPage(accountInfoList.subList(fromIndex, toIndex), accountInfoList.size());
  }

  @Override
  public AccountInfo getAccountInfo(int clientId) {
    ClientDot clientDot = db.get().clientStorage.get(clientId);

    AccountInfo accountInfo = new AccountInfo();
    accountInfo.id = clientDot.id;
    accountInfo.fullName = String.format("%s %s %s", clientDot.name, clientDot.surname, clientDot.patronymic);
    accountInfo.charm = getCharmById(clientDot.charmId);
    accountInfo.age = calculateYearDiff(clientDot.birthDate);

    ArrayList<Account> accounts = selectAccountsByClientId(accountInfo.id);
    if(accounts.size() == 0) return null;

    accountInfo.totalAccBalance = getTotalAccBalance(accounts);
    accountInfo.minAccBalance = getMinAccBalance(accounts);
    accountInfo.maxAccBalance = getMaxAccBalance(accounts);

    return accountInfo;
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
      if(accountDot.clientId == clientId && accountDot.isActive) accounts.add(accountDot.toAccount());
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
