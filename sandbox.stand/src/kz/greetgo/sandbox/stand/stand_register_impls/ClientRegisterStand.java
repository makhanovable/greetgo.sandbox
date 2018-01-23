package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public long getCount(String namefilter) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = this.getFilteredList(clientDots, namefilter);

    return clientDots.size() ;
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordListRequest listRequest) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    List<ClientRecord> clientRecords = new ArrayList<>();

    clientDots = this.getFilteredList(clientDots, listRequest.nameFilter);

    switch (listRequest.columnSortType) {
      case AGE:
        clientDots = this.getListByAge(clientDots, listRequest.sortAscend);
        break;
      case TOTALACCOUNTBALANCE:
        clientDots = this.getListByTotalAccountBalance(clientDots, listRequest.sortAscend);
        break;
      case MAXACCOUNTBALANCE:
        clientDots = this.getListByMaxAccountBalance(clientDots, listRequest.sortAscend);
        break;
      case MINACCOUNTBALANCE:
        clientDots = this.getListByMinAccountBalance(clientDots, listRequest.sortAscend);
        break;
      default:
        clientDots = this.getDefaultList(clientDots);
    }

    PageUtils.cutPage(clientDots,
      listRequest.clientRecordCountToSkip,
      listRequest.clientRecordCount);

    for (ClientDot clientDot : clientDots)
      clientRecords.add(clientDot.toClientRecord());

    return clientRecords;
  }

  private List<ClientDot> getFilteredList(List<ClientDot> clientDots, String nameFilter) {
    if (nameFilter == null || nameFilter.length() == 0)
      return clientDots;

    String loweredNameFilter = nameFilter.toLowerCase();

    Stream<ClientDot> stream = clientDots.stream().filter(new Predicate<ClientDot>() {
      @Override
      public boolean test(ClientDot clientDot) {
        if (clientDot.surname.toLowerCase().contains(loweredNameFilter) ||
          clientDot.lastname.toLowerCase().contains(loweredNameFilter) ||
          clientDot.patronymic.toLowerCase().contains(loweredNameFilter))
          return true;

        return false;
      }
    });

    clientDots = stream.collect(Collectors.toList());

    return clientDots;
  }

  private List<ClientDot> getDefaultList(List<ClientDot> clientDots) {
    return clientDots;
  }

  private List<ClientDot> getListByAge(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o1.age - o2.age;
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o2.age - o1.age;
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByTotalAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.totalAccountBalance).compareTo(o2.totalAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.totalAccountBalance).compareTo(o1.totalAccountBalance);
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByMaxAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.maxAccountBalance).compareTo(o2.maxAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.maxAccountBalance).compareTo(o1.maxAccountBalance);
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByMinAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    //TODO: lambda + Integer.comparator?
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o1.minAccountBalance).compareTo(o2.minAccountBalance);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Long.valueOf(o2.minAccountBalance).compareTo(o1.minAccountBalance);
        }
      });
    }

    return clientDots;
  }

  @Override
  public void removeRecord(long id) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;

    if (clientDotMap.remove(id) == null)
      throw new NotFound();
  }

  @Override
  public ClientDetails getDetails(Long id) {
    List<CharmDot> charmDots = new ArrayList<>(db.get().charmStorage.values());
    ClientDetails clientDetails;

    if (id == null) {
      clientDetails = new ClientDetails();

      clientDetails.id = null;
      clientDetails.surname = "";
      clientDetails.lastname = "";
      clientDetails.patronymic = "";
      clientDetails.gender = Gender.EMPTY;
      clientDetails.birthdate = "";
      clientDetails.charmId = charmDots.get(0).toCharm().id;

      clientDetails.registrationAddressInfo = new RegistrationAddressInfo();
      clientDetails.registrationAddressInfo.street = "";
      clientDetails.registrationAddressInfo.home = "";
      clientDetails.registrationAddressInfo.flat = "";

      clientDetails.residentialAddressInfo = new ResidentialAddressInfo();
      clientDetails.residentialAddressInfo.street = "";
      clientDetails.residentialAddressInfo.home = "";
      clientDetails.residentialAddressInfo.flat = "";

      clientDetails.phones = new ArrayList<>();
    } else {
      ClientDot clientDot = db.get().clientStorage.get(id);
      clientDetails = clientDot.toClientDetails();
    }

    for (CharmDot charmDot : charmDots)
      clientDetails.charmList.add(charmDot.toCharm());

    return clientDetails;
  }

  @Override
  public void saveDetails(ClientDetailsToSave detailstoSave) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;
    ClientDot clientDot;

    if (detailstoSave.id == null) {
      clientDot = new ClientDot();
      clientDot.toClientDot(detailstoSave, (long) clientDotMap.size() + 1, db.get().charmStorage);
      clientDotMap.put((long) clientDotMap.size() + 1, clientDot);
    } else {
      clientDot = clientDotMap.get(detailstoSave.id);
      clientDot.toClientDot(detailstoSave, null, db.get().charmStorage);
    }
  }
}
