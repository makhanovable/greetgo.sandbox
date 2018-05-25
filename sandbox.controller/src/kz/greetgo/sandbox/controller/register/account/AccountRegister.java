package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.ClientAccountInfo;
import kz.greetgo.sandbox.controller.model.ClientAccountInfoPage;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;

public interface AccountRegister {
  ClientAccountInfoPage getAllAccountInfo(TableRequestDetails requestDetails);
  ClientAccountInfo getAccountInfo(int clientId);
}
