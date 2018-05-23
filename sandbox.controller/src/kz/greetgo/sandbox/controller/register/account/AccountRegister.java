package kz.greetgo.sandbox.controller.register.account;

import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.AccountInfoPage;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.TableRequestDetails;

import java.util.List;

public interface AccountRegister {
  AccountInfoPage getAllAccountInfo(TableRequestDetails requestDetails);
  AccountInfo getAccountInfo(int clientId);
}
