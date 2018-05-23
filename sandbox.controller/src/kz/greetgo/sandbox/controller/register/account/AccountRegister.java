package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.register.account.model.AccountInfoPage;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;

public interface AccountRegister {
  AccountInfoPage getAllAccountInfo(TableRequestDetails requestDetails);
  AccountInfo getAccountInfo(int clientId);
}
