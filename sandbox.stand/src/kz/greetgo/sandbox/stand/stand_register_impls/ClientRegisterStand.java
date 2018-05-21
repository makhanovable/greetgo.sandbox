package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kz.greetgo.sandbox.stand.util.Constants.DUMB_ID;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<AccountRegister> accountRegister;

  @Override
  public ClientInfoModel getClientInfo(int clientId) {
    ClientInfoModel clientInfoModel = new ClientInfoModel();

    if (clientId != DUMB_ID) {
      clientInfoModel.clientInfo = db.get().clientStorage.get(clientId).toClient();
      clientInfoModel.factAddress = getAddress(clientId, AddressType.FACT);
      clientInfoModel.regAddress = getAddress(clientId, AddressType.REG);
      clientInfoModel.phones = getPhones(clientId);
    }

    clientInfoModel.charmsDictionary = getCharmsDictionary();

    return clientInfoModel;
  }

  @Override
  public AccountInfo createNewClient(String name,
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
                                     String phoneMobile3) {

    int newClientId = db.get().clientStorage.size() + 1;
    ClientDot newClient = new ClientDot(newClientId, name, surname, patronymic,
      Gender.valueOf(gender), new Date(birthDate), charmId);

    db.get().clientStorage.put(newClientId, newClient);

    addNewAddress(db.get().addressStorage.size() + 1, newClient.id, AddressType.FACT, streetFact, houseFact, flatFact);
    addNewAddress(db.get().addressStorage.size() + 1, newClient.id, AddressType.REG, streetReg, houseReg, flatReg);

    addDefaultAccount(newClient.id);

    addNewPhone(PhoneType.HOME, phoneHome, newClient.id);
    addNewPhone(PhoneType.WORK, phoneWork, newClient.id);
    addNewPhone(PhoneType.MOBILE, phoneMobile1, newClient.id);
    addNewPhone(PhoneType.MOBILE, phoneMobile2, newClient.id);
    addNewPhone(PhoneType.MOBILE, phoneMobile3, newClient.id);

    AccountInfo newAccountInfo = new AccountInfo();

    newAccountInfo.fullName = String.format("%s %s %s", name, surname, patronymic);
    newAccountInfo.charm = db.get().charmStorage.get(charmId).name;
    newAccountInfo.id = newClientId;

    return newAccountInfo;
  }

  @Override
  public AccountInfo editClient(int clientId,
                                String name,
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
                                String phoneMobile3) {

    ClientDot clientDot = db.get().clientStorage.get(clientId);
    if (clientDot == null) {
      throw new NullPointerException("no such client id:" + clientId);
    }

    clientDot.name = name;
    clientDot.surname = surname;
    clientDot.patronymic = patronymic;
    clientDot.birthDate = new Date(birthDate);
    clientDot.gender = Gender.valueOf(gender);
    clientDot.charmId = charmId;

    updateAddress(AddressType.FACT, clientId, streetFact, houseFact, flatFact);
    updateAddress(AddressType.REG, clientId, streetReg, houseReg, flatReg);

//  TODO: phone edit
//  TODO: birthdate edit

    return accountRegister.get().getAccountInfo(clientId);
  }

  private void updatePhone(PhoneType type, int clientId, String number) {
    Phone phone = getPhone(type, clientId);
//    TODO: COMPLETE
//
//    if(phone== null) {
//      PhoneDot phoneDot = new PhoneDot(db.get().phoneStorage.size() + 1,);
//      phoneDot.id = ;
//      db.get().phoneStorage.put(phoneDot.id, phoneDot);
//
//      phone = phoneDot.toPhone();
//    }
  }

  private void updateAddress(AddressType type, int clientId, String street, String house, String flat) {
    Address address = getAddress(clientId, type);

    if (address == null) {
      AddressDot addressDot = new AddressDot(db.get().addressStorage.size() + 1, clientId, type, street, house, flat);
      db.get().addressStorage.put(addressDot.id, addressDot);
      return;
    }

    address.street = street;
    address.house = house;
    address.flat = flat;
  }


  @Override
  public AccountInfo deleteClient(int clientId) {
    Client client = db.get().clientStorage.get(clientId).toClient();
    AccountInfo accountInfo = new AccountInfo();

    if (client == null) {
      throw new NullPointerException("client does not exist id:" + clientId);
    }
    db.get().clientStorage.remove(clientId);

    removeAllAccounts(clientId);
    removeAllAddresses(clientId);
    removeAllPhones(clientId);

    accountInfo.id = clientId;
    return accountInfo;
  }

  private void addNewPhone(PhoneType type, String number, int clientId) {
    if (number == null || number.isEmpty() || clientId == DUMB_ID) {
      return;
//      throw new NullPointerException("add number clientId:" + clientId);
    }

    int newPhoneId = db.get().phoneStorage.size() + 1;
    db.get().phoneStorage.put(newPhoneId, new PhoneDot(newPhoneId, clientId, number, type));
  }

  private void addDefaultAccount(int clientId) {
    int newAccountId = db.get().accountStorage.size() + 1;

    db.get().accountStorage.put(newAccountId,
      new AccountDot(newAccountId, clientId, 0f, "KZT!@#$",
        new java.sql.Timestamp(Date.from(Instant.now()).getTime())));

  }

  private void addNewAddress(int id, int clientId, AddressType type, String street, String house, String flat) {
    db.get().addressStorage.put(id, new AddressDot(id, clientId, type, street, house, flat));
  }

  private void removeAllAddresses(int clientId) {
    db.get().addressStorage.values().removeIf(address -> address.clientId == clientId);
  }

  private void removeAllPhones(int clientId) {
    db.get().phoneStorage.values().removeIf(phone -> phone.clientId == clientId);
  }

  private void removeAllAccounts(int clientId) {
    db.get().accountStorage.values().removeIf(account -> account.clientId == clientId);
  }

  private List<Charm> getCharmsDictionary() {
    List<Charm> result = new ArrayList<>();

    for (CharmDot charmDot : db.get().charmStorage.values()) {
      result.add(charmDot.toCharm());
    }

    return result;
  }

  private Address getAddress(int clientId, AddressType addressType) {
    for (AddressDot addressDot : db.get().addressStorage.values()) {
      if (addressDot.clientId == clientId && addressDot.addressType == addressType) {
        return addressDot.toAddress();
      }
    }

    return null;
  }

  private List<Phone> getPhones(int clientId) {
    List<Phone> result = new ArrayList<>();

    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId)
        result.add(phoneDot.toPhone());
    }

    return result;
  }

  private Phone getPhone(PhoneType type, int clientId) {
    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId && phoneDot.type == type) {
        return phoneDot.toPhone();
      }
    }

    return null;
  }

}
