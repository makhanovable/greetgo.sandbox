package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidCharmError;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.model.AccountInfoPage;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<AccountRegister> accountRegister;

  @Override
  public ClientInfo getClientInfo(int clientId) {
    ClientInfo clientInfo = new ClientInfo();

    clientInfo.client = db.get().clientStorage.get(clientId).toClient();

    AddressDot factAddDot = getAddressDot(clientId, AddressType.FACT);
    if (factAddDot != null) clientInfo.factAddress = factAddDot.toAddress();

    AddressDot regAddDot = getAddressDot(clientId, AddressType.REG);
    if (regAddDot != null) clientInfo.regAddress = regAddDot.toAddress();

    clientInfo.phones = getPhones(clientId);

    return clientInfo;
  }

  @Override
  public AccountInfo createNewClient(ClientInfo clientInfo) {

    int newClientId = db.get().clientStorage.size() + 1;

    createNewClientDot(newClientId, clientInfo.client);

    createNewAddressDot(db.get().addressStorage.size() + 1, newClientId, clientInfo.factAddress);
    createNewAddressDot(db.get().addressStorage.size() + 1, newClientId, clientInfo.regAddress);

    createDefaultAccountDot(newClientId);

    for (Phone phone : clientInfo.phones)
      createNewPhoneDot(phone.type, phone.number, newClientId);

    return accountRegister.get().getAccountInfo(newClientId);
  }

  @Override
  public AccountInfo editClient(ClientInfo clientInfo) {

    ClientDot clientDot = db.get().clientStorage.get(clientInfo.client.id);
    if (clientDot == null) {
      throw new NullPointerException("no such client, id:" + clientInfo.client.id);
    }

    updateClient(clientDot, clientInfo.client);

    updateAddress(AddressType.FACT, clientDot.id, clientInfo.factAddress);
    updateAddress(AddressType.REG, clientDot.id, clientInfo.regAddress);

    updatePhones(clientDot.id, clientInfo.phones);

    return accountRegister.get().getAccountInfo(clientDot.id);
  }

  @Override
  public AccountInfoPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    Client client = db.get().clientStorage.get(clientId).toClient();

    if (client == null) {
      throw new NullPointerException("client does not exist id:" + clientId);
    }

    disableClientDot(clientId);
    removeAllAccounts(clientId);
    removeAllAddresses(clientId);
    removeAllPhones(clientId);

    return accountRegister.get().getAllAccountInfo(requestDetails);
  }

  private void disableClientDot(int clientId) {
    ClientDot clientDot = db.get().clientStorage.get(clientId);
    clientDot.isActive = false;
  }

  private AddressDot getAddressDot(int clientId, AddressType addressType) {
    for (AddressDot addressDot : db.get().addressStorage.values()) {
      if (addressDot.clientId == clientId && addressDot.addressType == addressType) {
        return addressDot;
      }
    }

    return null;
  }

  private List<Phone> getPhones(int clientId) {
    List<Phone> result = new ArrayList<>();

    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId && phoneDot.isActive)
        result.add(phoneDot.toPhone());
    }

    return result;
  }

  private void createNewClientDot(int clientId, Client client) {
    CharmDot charmDot = db.get().charmStorage.get(client.charmId);
    if(charmDot == null) throw new InvalidCharmError(404, "Invalid charm, please, choose another one");

    ClientDot newClientDot = new ClientDot(clientId, client.name,
      client.surname, client.patronymic, client.gender, client.birthDate, client.charmId);

    db.get().clientStorage.put(clientId, newClientDot);
  }

  private void createNewAddressDot(int id, int clientId, Address address) {
    if (address.house.isEmpty() && address.street.isEmpty() && address.flat.isEmpty())
      return;
    
    db.get().addressStorage.put(id, new AddressDot(
      id, clientId, address.type, address.street, address.house, address.flat));
  }

  private void createDefaultAccountDot(int clientId) {
    int newAccountId = db.get().accountStorage.size() + 1;

    db.get().accountStorage.put(newAccountId,
      new AccountDot(newAccountId, clientId,
        0f, "KZT!@#$", new java.sql.Timestamp(Date.from(Instant.now()).getTime())));
  }

  private void createNewPhoneDot(PhoneType type, String number, int clientId) {
    int newPhoneId = db.get().phoneStorage.size() + 1;
    db.get().phoneStorage.put(newPhoneId, new PhoneDot(newPhoneId, clientId, number, type));
  }

  private void updateClient(ClientDot clientDot, Client client) {
    CharmDot charmDot = db.get().charmStorage.get(client.charmId);
    if(charmDot == null) throw new InvalidCharmError(404, "Invalid charm, please, choose another one");

    clientDot.name = client.name;
    clientDot.surname = client.surname;
    clientDot.patronymic = client.patronymic;
    clientDot.birthDate = client.birthDate;
    clientDot.gender = client.gender;
    clientDot.charmId = client.charmId;
  }

  private void updateAddress(AddressType type, int clientId, Address address) {
    AddressDot addressDot = getAddressDot(clientId, type);

    if (addressDot == null) {
      addressDot = new AddressDot(db.get().addressStorage.size() + 1, clientId, type, address.street, address.house, address.flat);
      db.get().addressStorage.put(addressDot.id, addressDot);
      return;
    }

    addressDot.street = address.street;
    addressDot.house = address.house;
    addressDot.flat = address.flat;
  }

  private void updatePhones(int clientId, List<Phone> phones) {
    for (Phone phone : phones) {
      PhoneDot phoneDot = getPhoneDot(phone.type, clientId, phone.number);

      if (phoneDot != null && !phoneDot.number.equals(phone.number)) {

        phoneDot.isActive = false;
        createNewPhoneDot(phone.type, phone.number, clientId);

      } else if (phoneDot == null) {
        createNewPhoneDot(phone.type, phone.number, clientId);
      }
    }

    // All other phones should be disabled
    for (PhoneDot phoneDot : getPhoneDots(clientId)) {
      if (!containsNumber(phones, phoneDot.number)) {
        phoneDot.isActive = false;
      }
    }
  }

  private boolean containsNumber(final List<Phone> list, final String number) {
    return list.stream().anyMatch(o -> o.number.equals(number));
  }

  private PhoneDot getPhoneDot(PhoneType type, int clientId, String number) {
    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId && phoneDot.type == type && phoneDot.number.equals(number)) {
        return phoneDot;
      }
    }

    return null;
  }

  private List<PhoneDot> getPhoneDots(int clientId) {
    List<PhoneDot> mobiles = new ArrayList<>();
    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId) {
        mobiles.add(phoneDot);
      }
    }

    return mobiles;
  }

  private void removeAllAccounts(int clientId) {
    for (AccountDot accountDot : db.get().accountStorage.values()) {
      if (accountDot.clientId == clientId) {
        accountDot.isActive = false;
      }
    }
  }

  private void removeAllAddresses(int clientId) {
    for (AddressDot addressDot : db.get().addressStorage.values()) {
      if (addressDot.clientId == clientId) {
        addressDot.isActive = false;
      }
    }
  }

  private void removeAllPhones(int clientId) {
    for (PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if (phoneDot.clientId == clientId) {
        phoneDot.isActive = false;
      }
    }
  }
}
