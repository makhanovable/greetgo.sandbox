package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientReport;
import kz.greetgo.sandbox.controller.report.ClientReportXLSX;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Bean
public class ClientRegisterStand implements ClientRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<StandDb> db;
  private IdGenerator gen = new IdGenerator();

  @Override
  public void addOrUpdate(ClientToSave clientToSave) {
    ClientDot clientDot = new ClientDot(clientToSave);
    if (clientDot.id == null)
      clientDot.id = this.gen.newId();

    setClientData(clientDot, clientToSave);
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void generateReport(OutputStream out, String type, String orderBy, int order, String filter) throws Exception {
    ClientReport clientReport = null;
    String filename = "report" + gen.newId() + "." + type;
    File file = new File(filename);

    Map<String, String> charms = new HashMap<>();
    for (CharmDot charmDot : db.get().charmStorage.values())
      charms.put(charmDot.id, charmDot.name);

    switch (type) {
      case "pdf":
        throw new NotImplementedException();

//        break;
      case "xlsx":
        clientReport = new ClientReportXLSX(charms);
        break;
    }

    if (clientReport != null) {
      long records = this.getClientsSize(filter);
      int chunk = 100;

      for (int page = 0; page < Math.ceil(records / (double) chunk); page++) {
        clientReport.appendData(this.getClientInfoList(chunk, page, filter, orderBy, order));
      }

      clientReport.write(out);
    } else {
      throw new NotImplementedException();
    }
    

  }

  @Override
  public ClientDetail detail(String id) {
    ClientDetail clientDetail = this.db.get().clientStorage.get(id).toClientForm();
    this.setDetails(clientDetail);
    return clientDetail;
  }


  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {

    //filtering
    List<ClientRecord> list = this.getFilteredClientInfo(filter);
    int clientInfoSize = list.size();
    if (limit * page > clientInfoSize)
      return null;

    for (ClientRecord clientRecord : list) {

      List<ClientAccountDot> clientAccountDots = this.db.get().clientAccountStorage.get(clientRecord.id);
      if (clientAccountDots != null) {
        ClientAccountDot[] arr = new ClientAccountDot[clientAccountDots.size()];
        clientAccountDots.toArray(arr);

        clientRecord.totalAccountBalance = arr[0].money;
        clientRecord.minimumBalance = arr[0].money;
        clientRecord.maximumBalance = arr[0].money;

        for (int i = 1; i < arr.length; i++) {
          clientRecord.totalAccountBalance += arr[i].money;
          if (clientRecord.minimumBalance > arr[0].money)
            clientRecord.minimumBalance = arr[0].money;
          if (clientRecord.maximumBalance < arr[0].money)
            clientRecord.maximumBalance = arr[0].money;
        }
      }


    }

    //sorting
    String order = orderBy != null ? orderBy.toLowerCase().trim() : "";
    list.sort((o1, o2) -> {
      if (desc == 1) {
        ClientRecord tmp = o1;
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

    return list.subList(fromIndex, endindex);
  }

  @Override
  public long getClientsSize(String filter) {

    return this.getFilteredClientInfo(filter).size();
  }

  @Override
  public int remove(List<String> ids) {

    int success = 0;
    for (String id : ids) {
      success += remove(id) ? 1 : 0;
    }
    return success;
  }

  private boolean remove(String id) {
    db.get().clientAddressStorage.remove(id);
    db.get().clientPhoneNumberStorage.remove(id);
    return db.get().clientStorage.remove(id) != null;
  }

  private List<ClientRecord> getFilteredClientInfo(String filter) {
    List<ClientRecord> list = new ArrayList<>();
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    if (filter != null) {
      String[] filters = filter.trim().split(" ");

      for (ClientDot clientDot : clientDots) {
        for (String f : filters)
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

  private void setClientData(ClientDot clientDot, ClientToSave clientToSave) {
    db.get().clientStorage.put(clientDot.id, clientDot);
    List<ClientAddressDot> addresses = new ArrayList<>();
    if (clientToSave.actualAddress != null) {
      addresses.add(new ClientAddressDot(clientDot.id, clientToSave.actualAddress));
    }
    if (clientToSave.registerAddress != null) {
      addresses.add(new ClientAddressDot(clientDot.id, clientToSave.registerAddress));
    }
    db.get().clientAddressStorage.put(clientDot.id, addresses);

    List<ClientPhoneNumberDot> numbers = db.get().clientPhoneNumberStorage.get(clientDot.id);


    for (ClientPhoneNumber number : clientToSave.numbersToDelete) {
      ClientPhoneNumberDot found = numbers.stream().filter(o -> o.number.equals(number.number)).findFirst().get();
      numbers.remove(found);
    }

    for (ClientPhoneNumberToSave number : clientToSave.numersToSave) {
      if (number.oldNumber != null) {
        Boolean found = numbers.stream().anyMatch(o -> o.number.equals(number.oldNumber));
        if (found) {
          ClientPhoneNumberDot clientPhoneNumberDot = numbers.stream().filter(o -> o.number.equals(number.oldNumber)).findFirst().get();
          numbers.remove(clientPhoneNumberDot);
        }

      }
      numbers.add(new ClientPhoneNumberDot(clientDot.id, number));

    }
    db.get().clientPhoneNumberStorage.put(clientDot.id, numbers);
  }

  private void setDetails(ClientDetail clientDetail) {
    List<ClientAddressDot> addresses = db.get().clientAddressStorage.get(clientDetail.id);
    if (addresses != null) {
      for (ClientAddressDot address : addresses) {
        if (address.type == AddressType.REG) {
          clientDetail.registerAddress = address.toClientAddress();
        } else if (address.type == AddressType.FACT) {
          clientDetail.actualAddress = address.toClientAddress();
        }
      }
    }

    List<ClientPhoneNumberDot> numbers = db.get().clientPhoneNumberStorage.get(clientDetail.id);
    if (numbers != null) {

      clientDetail.phoneNumbers = new ArrayList<>();
      for (ClientPhoneNumberDot dot : numbers) {
        clientDetail.phoneNumbers.add(dot.toClientPhoneNumber());
      }
    }
  }

}
