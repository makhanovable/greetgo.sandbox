package kz.greetgo.sandbox.controller.register.info;

import kz.greetgo.sandbox.controller.model.AccountInfo;

public interface AccountInfoRegister {
  AccountInfo[] getAllAccountInfo();
  String healthCheck();
}
