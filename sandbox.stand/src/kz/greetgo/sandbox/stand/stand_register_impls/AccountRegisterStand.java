package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.register.account.model.AccountInfoPage;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;

@Bean
public class AccountRegisterStand implements AccountRegister {

  public BeanGetter<StandDb> db;

  @Override
  public AccountInfoPage getAllAccountInfo(TableRequestDetails requestDetails) {
    ArrayList<AccountInfo> accountInfoList = new ArrayList<>();

    for (ClientDot clientDot : db.get().clientStorage.values()) {
      if (clientDot.isActive) {
        AccountInfo accountInfo = getAccountInfo(clientDot.id);
        accountInfoList.add(accountInfo);
      }
    }

    accountInfoList = filter(accountInfoList, requestDetails.filter);
    accountInfoList = sort(accountInfoList, requestDetails.sortBy, requestDetails.sortDirection);

    int totalAccountInfoCount = accountInfoList.size();

    accountInfoList = paginate(accountInfoList, requestDetails.pageIndex, requestDetails.pageSize);


    return new AccountInfoPage(accountInfoList, totalAccountInfoCount);
  }

  private ArrayList<AccountInfo> filter(ArrayList<AccountInfo> list, String filterValue) {
    return list.stream()
      .filter(a -> a.fullName.replaceAll("\\s+", "").toLowerCase()
        .contains(filterValue.replaceAll("\\s+", "").toLowerCase())
      ).collect(Collectors.toCollection(ArrayList::new));
  }

  private ArrayList<AccountInfo> sort(ArrayList<AccountInfo> list, SortColumn column, SortDirection direction) {

    switch (column) {
      case FIO:
        list.sort(Comparator.comparing(a -> a.fullName));
        break;
      case AGE:
        list.sort(Comparator.comparingInt(a -> a.age));
        break;
      case TOTAL:
        list.sort((AccountInfo a1, AccountInfo a2) -> (int) (a1.totalAccBalance - a2.totalAccBalance));
        break;
      case MAX:
        list.sort((AccountInfo a1, AccountInfo a2) -> (int) (a1.maxAccBalance - a2.maxAccBalance));
        break;
      case MIN:
        list.sort((AccountInfo a1, AccountInfo a2) -> (int) (a1.minAccBalance - a2.minAccBalance));
        break;
    }

    if (column != SortColumn.NONE && direction == SortDirection.DESC) Collections.reverse(list);

    return list;
  }

  private ArrayList<AccountInfo> paginate(ArrayList<AccountInfo> list, int pageIndex, int pageSize) {
    int fromIndex = pageIndex * pageSize;
    int toIndex = fromIndex + pageSize;
    if (toIndex > list.size()) toIndex = list.size();

    return new ArrayList<>(list.subList(fromIndex, toIndex));
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
    if (accounts.size() == 0) return null;

    accountInfo.totalAccBalance = getTotalAccBalance(accounts);
    accountInfo.minAccBalance = getMinAccBalance(accounts);
    accountInfo.maxAccBalance = getMaxAccBalance(accounts);

    return accountInfo;
  }

  private float getMinAccBalance(ArrayList<Account> accounts) {
    float result = accounts.get(0).money;
    for (int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if (curAcc.money < result) result = curAcc.money;
    }
    return result;
  }

  private float getMaxAccBalance(ArrayList<Account> accounts) {
    float result = accounts.get(0).money;
    for (int i = 1; i < accounts.size(); i++) {
      Account curAcc = accounts.get(i);
      if (curAcc.money > result) result = curAcc.money;
    }
    return result;
  }


  private float getTotalAccBalance(ArrayList<Account> accounts) {
    float result = 0f;
    for (Account acc : accounts) {
      result += acc.money;
    }
    return result;
  }

  private ArrayList<Account> selectAccountsByClientId(int clientId) {
    ArrayList<Account> accounts = new ArrayList<>();

    for (AccountDot accountDot : db.get().accountStorage.values()) {
      if (accountDot.clientId == clientId && accountDot.isActive) accounts.add(accountDot.toAccount());
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
