package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.AccountInfo;

import java.util.List;

public interface AccountInfoRegister {
  List<AccountInfo> getAllAccountInfo();
  String healthCheck();
}
