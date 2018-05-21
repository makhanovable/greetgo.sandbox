package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.client.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public ClientInfoModel getClientInfo(int clientId) {
    ClientInfoModel clientInfoModel = new ClientInfoModel();

    if(clientId != -1) {
      clientInfoModel.clientInfo = db.get().clientStorage.get(clientId).toClient();
      clientInfoModel.factAddress = getAddress(clientId, AddressType.FACT);
      clientInfoModel.regAddress = getAddress(clientId, AddressType.REG);
      clientInfoModel.phones = getPhones(clientId);
    }

    clientInfoModel.charmsDictionary = getCharmsDictionary();

    return clientInfoModel;
  }

  private List<Charm> getCharmsDictionary() {
    List<Charm> result = new ArrayList<>();

    for(CharmDot charmDot : db.get().charmStorage.values()) {
      result.add(charmDot.toCharm());
    }

    return result;
  }

  private Address getAddress(int clientId, AddressType addressType) {
    for(AddressDot addressDot : db.get().addressStorage.values()) {
      if(addressDot.clientId == clientId && addressDot.addressType == addressType) {
        return addressDot.toAddress();
      }
    }

    return null;
  }

  private List<Phone> getPhones(int clientId) {
    List<Phone> result = new ArrayList<>();

    for(PhoneDot phoneDot : db.get().phoneStorage.values()) {
      if(phoneDot.clientId == clientId)
        result.add(phoneDot.toPhone());
    }

    return result;
  }

}
