package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.AccountInfo;

import java.util.List;

public class AccountInfoPage {
  public List<AccountInfo> accountInfoList;
  public int totalAccountInfo;

  public AccountInfoPage(List<AccountInfo> accountInfoList, int totalAccountInfo) {
    this.accountInfoList = accountInfoList;
    this.totalAccountInfo = totalAccountInfo;
  }
}
