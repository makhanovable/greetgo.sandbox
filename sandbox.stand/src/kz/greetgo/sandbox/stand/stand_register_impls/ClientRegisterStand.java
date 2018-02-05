package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<ReportRegister> clientListReportRegister;

  @Override
  public long getCount(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = PageUtils.getFilteredList(clientDots, request.nameFilter);

    return clientDots.size();
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    List<ClientRecord> clientRecords = new ArrayList<>();

    clientDots = PageUtils.getFilteredList(clientDots, request.nameFilter);
    clientDots = PageUtils.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    PageUtils.cutPage(clientDots,
      request.clientRecordCountToSkip,
      request.clientRecordCount);

    for (ClientDot clientDot : clientDots)
      clientRecords.add(clientDot.toClientRecord());

    return clientRecords;
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
      clientDetails.name = "";
      clientDetails.patronymic = "";
      clientDetails.gender = Gender.EMPTY;
      clientDetails.birthdate = "";
      clientDetails.charmId = charmDots.get(0).toCharm().id;

      clientDetails.registrationAddressInfo = new AddressInfo();
      clientDetails.registrationAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.registrationAddressInfo.street = "";
      clientDetails.registrationAddressInfo.house = "";
      clientDetails.registrationAddressInfo.flat = "";

      clientDetails.factualAddressInfo = new AddressInfo();
      clientDetails.factualAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.factualAddressInfo.street = "";
      clientDetails.factualAddressInfo.house = "";
      clientDetails.factualAddressInfo.flat = "";

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
  public void saveDetails(ClientDetailsToSave detailsToSave) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;
    ClientDot clientDot;
    long id = db.get().curClientId.getAndIncrement();
    db.get().curClientId.set(id + 1);

    if (detailsToSave.id == null) {
      clientDot = new ClientDot();
      clientDot.toClientDot(detailsToSave, id, db.get().charmStorage);
      clientDotMap.put(id, clientDot);
    } else {
      clientDot = clientDotMap.get(detailsToSave.id);
      clientDot.toClientDot(detailsToSave, null, db.get().charmStorage);
    }
  }
}
