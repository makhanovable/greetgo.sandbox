package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Bean
public class ClientRegisterStand implements ClientRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<StandDb> db;
  IdGenerator gen = new IdGenerator();

  @Override
  public boolean update(ClientForm clientForm) {
    if (db.get().clientStorage.get(clientForm.id) == null)
      return false;
    ClientDot clientDot = new ClientDot(clientForm);

    setClientData(clientDot, clientForm);
    return true;
  }

  @Override
  public void add(ClientForm clientForm) {
    ClientDot clientDot = new ClientDot(clientForm);
    clientDot.id = this.gen.newId();
    setClientData(clientDot, clientForm);
  }

  private void setClientData(ClientDot clientDot, ClientForm clientForm) {
    db.get().clientStorage.put(clientDot.id, clientDot);

    List<ClientAddressDot> addresses = new ArrayList<>();
    if (clientForm.registerAddress != null)
      addresses.add(new ClientAddressDot(clientDot.id, clientForm.registerAddress));
    if (clientForm.actualAddress != null)
      addresses.add(new ClientAddressDot(clientDot.id, clientForm.actualAddress));

    db.get().clientAddressStorage.put(clientDot.id, addresses);

    List<ClientPhoneNumberDot> numbers = new ArrayList<>();
    for (ClientPhoneNumber number : clientForm.phoneNumbers)
      numbers.add(new ClientPhoneNumberDot(clientDot.id, number));

//        db.get().clientPhoneNumberStorage.
    db.get().clientPhoneNumberStorage.put(clientDot.id, numbers);

  }

  @Override
  public ClientForm info(String id) {
    ClientForm clientForm = this.db.get().clientStorage.get(id).toClientForm();
    this.setDetails(clientForm);
    return clientForm;
  }

  private void setDetails(ClientForm clientForm) {
    List<ClientAddressDot> addresses = db.get().clientAddressStorage.get(clientForm.id);
    if (addresses != null) {

      for (ClientAddressDot address : addresses) {
        //Убери систем аут. Используй логгеры вместо них
        System.out.println(address.type);

        if (address.type == AddressType.REG) {
          clientForm.registerAddress = address.toClientAddress();
        } else if (address.type == AddressType.FACT) {
          clientForm.actualAddress = address.toClientAddress();
        }
      }
    }

    List<ClientPhoneNumberDot> numbers = db.get().clientPhoneNumberStorage.get(clientForm.id);
    if (numbers != null) {

      clientForm.phoneNumbers = new ArrayList<>();
      for (ClientPhoneNumberDot dot : numbers) {
        clientForm.phoneNumbers.add(dot.toClientPhoneNumber());
      }
    }
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public List<ClientInfo> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {

    //filtering
    List<ClientInfo> list = this.getFilteredClientInfo(filter);

    int clientInfoSize = list.size();

    if (limit * page > clientInfoSize)
      return null;

    //sorting
    String order = orderBy != null ? orderBy.toLowerCase().trim() : "";
    list.sort((o1, o2) -> {
      if (desc == 1) {
        ClientInfo tmp = o1;
        o1 = o2;
        o2 = tmp;
      }
      switch (order) {
        case "age":
          return Integer.compare(o1.age, o2.age);
        case "totalaccountbalance":
          return Float.compare(o1.totalAccountBalance, o2.totalAccountBalance);
        case "maximumbalance":
          return Float.compare(o1.maximumBalance, o2.maximumBalance);
        case "minimumbalance":
          return Float.compare(o1.minimumBalance, o2.minimumBalance);
        default:
          String fio1 = new StringBuilder(o1.name.toLowerCase()).append(o1.surname.toLowerCase()).append(o1.patronymic.toLowerCase()).toString();
          String fio2 = new StringBuilder(o2.name.toLowerCase()).append(o2.surname.toLowerCase()).append(o2.patronymic.toLowerCase()).toString();
          return fio1.compareTo(fio2);
      }
    });

    //splicing
    int fromIndex = limit * page;
    int endindex = fromIndex + limit <= clientInfoSize ? fromIndex + limit : clientInfoSize;

    List<ClientInfo> subList = list.subList(fromIndex, endindex);

    for (ClientInfo clientInfo : subList) {

      if (db.get().clientAccountStorage.entrySet().iterator().hasNext()) {
        Map.Entry<String, ClientAccountDot> entry = db.get().clientAccountStorage.entrySet().iterator().next();
        clientInfo.totalAccountBalance = entry.getValue().money;
        clientInfo.maximumBalance = entry.getValue().money;
        clientInfo.minimumBalance = entry.getValue().money;
      }

      for (ClientAccountDot account : db.get().clientAccountStorage.values()) {
        clientInfo.totalAccountBalance += account.money;
        if (clientInfo.maximumBalance < account.money)
          clientInfo.maximumBalance = account.money;
        if (clientInfo.minimumBalance > account.money)
          clientInfo.minimumBalance = account.money;
      }
    }
    return subList;
  }

  @Override
  public int getClientsSize(String filter) {

    return this.getFilteredClientInfo(filter).size();
  }

  @Override
  public float remove(List<String> ids) {

    int success = 0;
    for (String id : ids) {
      success += remove(id) ? 1 : 0;
    }
    return success / ids.size();
  }

  private boolean remove(String id) {
    db.get().clientAddressStorage.remove(id);
    db.get().clientPhoneNumberStorage.remove(id);
    return db.get().clientStorage.remove(id) != null;
  }

  private List<ClientInfo> getFilteredClientInfo(String filter) {
    List<ClientInfo> list = new ArrayList<>();
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    if (filter != null) {
      String[] filsers = filter.trim().split(" ");

      for (ClientDot clientDot : clientDots) {
        for (String f : filsers)
          if (clientDot.getFIO().toLowerCase().contains(f.toLowerCase())) {
            list.add(clientDot.toClientInfo());
            break;
          }
      }

    } else {
      for (ClientDot clientDot : clientDots)
        list.add(clientDot.toClientInfo());
    }
    return list;
  }

}
