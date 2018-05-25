package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidCharmError;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.account.AccountRegister;
import kz.greetgo.sandbox.controller.model.ClientAccountInfoPage;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
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
  public BeanGetter<CharmRegister> charmRegister;

  @Override
  public ClientDetails getClientDetails(int clientId) {
    ClientDetails clientDetails = new ClientDetails();

    clientDetails.charmsDictionary = charmRegister.get().getCharmDictionary();

    ClientDot clientDot = db.get().clientStorage.get(clientId);
    if(clientDot == null) return clientDetails;

    clientDetails.id = clientDot.id;
    clientDetails.name = clientDot.name;
    clientDetails.surname = clientDot.surname;
    clientDetails.patronymic = clientDot.patronymic;
    clientDetails.gender = clientDot.gender;
    clientDetails.birthDate = clientDot.birthDate;
    clientDetails.charmId= clientDot.charmId;

    AddressDot factAddDot = getAddressDot(clientId, AddressType.FACT);
    if (factAddDot != null) clientDetails.factAddress = factAddDot.toAddress();

    AddressDot regAddDot = getAddressDot(clientId, AddressType.REG);
    if (regAddDot != null) clientDetails.regAddress = regAddDot.toAddress();

    clientDetails.phones = getPhones(clientId);

    return clientDetails;
  }

  @Override
  public ClientAccountInfo createNewClient(ClientToSave clientToSave) {

    int newClientId = db.get().clientStorage.size() + 1;

    createNewClientDot(newClientId, clientToSave);

    createNewAddressDot(db.get().addressStorage.size() + 1, newClientId, clientToSave.factAddress);
    createNewAddressDot(db.get().addressStorage.size() + 1, newClientId, clientToSave.regAddress);

    createDefaultAccountDot(newClientId);

    for (Phone phone : clientToSave.phones)
      createNewPhoneDot(phone.type, phone.number, newClientId);

    return accountRegister.get().getAccountInfo(newClientId);
  }

  @Override
  public ClientAccountInfo editClient(ClientToSave clientToSave) {

    ClientDot clientDot = db.get().clientStorage.get(clientToSave.id);
    if (clientDot == null) {
      throw new NullPointerException("no such client, id:" + clientToSave.id);
    }

    updateClient(clientDot, clientToSave);

    updateAddress(AddressType.FACT, clientDot.id, clientToSave.factAddress);
    updateAddress(AddressType.REG, clientDot.id, clientToSave.regAddress);

    updatePhones(clientDot.id, clientToSave.phones);

    return accountRegister.get().getAccountInfo(clientDot.id);
  }

  @Override
  public ClientAccountInfoPage deleteClient(int clientId, TableRequestDetails requestDetails) {
    ClientDot client = db.get().clientStorage.get(clientId);

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

  private void createNewClientDot(int clientId, ClientToSave clientToSave) {
    CharmDot charmDot = db.get().charmStorage.get(clientToSave.charmId);
    if(charmDot == null) throw new InvalidCharmError(404, "Invalid charm, please, choose another one");

    ClientDot newClientDot = new ClientDot(clientId, clientToSave.name,
      clientToSave.surname, clientToSave.patronymic, clientToSave.gender, clientToSave.birthDate, clientToSave.charmId);

    db.get().clientStorage.put(clientId, newClientDot);
  }

  private void createNewAddressDot(int id, int clientId, Address address) {
    if (address == null || (address.house.isEmpty() && address.street.isEmpty() && address.flat.isEmpty()))
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

  private void updateClient(ClientDot clientDot, ClientToSave clientToSave) {
    CharmDot charmDot = db.get().charmStorage.get(clientToSave.charmId);
    if(charmDot == null) throw new InvalidCharmError(404, "Invalid charm, please, choose another one");

    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    clientDot.birthDate = clientToSave.birthDate;
    clientDot.gender = clientToSave.gender;
    clientDot.charmId = clientToSave.charmId;
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
