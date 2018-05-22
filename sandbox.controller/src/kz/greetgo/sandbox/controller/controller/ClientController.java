package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.AccountInfo;
import kz.greetgo.sandbox.controller.model.ClientInfoModel;
import kz.greetgo.sandbox.controller.model.InfoForm;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.Date;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @Mapping("/info")
  public ClientInfoModel getClientInfo(@Par("clientId") int clientId) {
    return clientRegister.get().getClientInfo(clientId);
  }
// TODO: refactor
  @ToJson()
  @Mapping("/create")
  public AccountInfo createNewClient(
    @Par("name") String name,
    @Par("surname") String surname,
    @Par("patronymic") String patronymic,
    @Par("gender") String gender,
    @Par("birthDate") Long birthDate,
    @Par("charmId") int charmId,
    @Par("streetFact") String streetFact,
    @Par("houseFact") String houseFact,
    @Par("flatFact") String flatFact,
    @Par("streetReg") String streetReg,
    @Par("houseReg") String houseReg,
    @Par("flatReg") String flatReg,
    @Par("phoneHome") String phoneHome,
    @Par("phoneWork") String phoneWork,
    @Par("phoneMobile1") String phoneMobile1,
    @Par("phoneMobile2") String phoneMobile2,
    @Par("phoneMobile3") String phoneMobile3) {

    return clientRegister.get().createNewClient(new InfoForm());
  }

  @ToJson()
  @Mapping("/edit")
  public AccountInfo editClient(
    @Par("clientId") int clientId,
    @Par("name") String name,
    @Par("surname") String surname,
    @Par("patronymic") String patronymic,
    @Par("gender") String gender,
    @Par("birthDate") Long birthDate,
    @Par("charmId") int charmId,
    @Par("streetFact") String streetFact,
    @Par("houseFact") String houseFact,
    @Par("flatFact") String flatFact,
    @Par("streetReg") String streetReg,
    @Par("houseReg") String houseReg,
    @Par("flatReg") String flatReg,
    @Par("phoneHome") String phoneHome,
    @Par("phoneWork") String phoneWork,
    @Par("phoneMobile1") String phoneMobile1,
    @Par("phoneMobile2") String phoneMobile2,
    @Par("phoneMobile3") String phoneMobile3) {

    return clientRegister.get().editClient(new InfoForm());
  }

  @ToJson()
  @Mapping("/delete")
  public AccountInfo deleteClient(@Par("clientId") int clientId) {
    return clientRegister.get().deleteClient(clientId);
  }
}
