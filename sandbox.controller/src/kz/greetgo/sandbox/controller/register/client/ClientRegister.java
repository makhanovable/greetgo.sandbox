package kz.greetgo.sandbox.controller.register.client;


import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfoModel;

import java.util.Date;

public interface ClientRegister {
  ClientInfoModel getClientInfo(int clientId);
  AccountInfo createNewClient(String name,
                              String surname,
                              String patronymic,
                              String gender,
                              Long birthDate,
                              int charmId,
                              String streetFact,
                              String houseFact,
                              String flatFact,
                              String streetReg,
                              String houseReg,
                              String flatReg,
                              String phoneHome,
                              String phoneWork,
                              String phoneMobile1,
                              String phoneMobile2,
                              String phoneMobile3);
}
