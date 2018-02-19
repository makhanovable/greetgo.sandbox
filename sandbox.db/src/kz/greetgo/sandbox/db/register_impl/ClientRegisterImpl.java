package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

// FIXME: 2/19/18 не импортируй весь класс, а только нужные.
import java.util.*;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {

    if (limit == 0) return new ArrayList<>();

    String[] orders = {"age", "totalAccountBalance", "maximumBalance", "minimumBalance"};
    Boolean match = orderBy != null && Arrays.stream(orders).anyMatch(o -> o.equals(orderBy));

    String ob = match ? orderBy : "concat(name, surname, patronymic)";
    limit = limit > 100 ? 100 : limit;
    int offset = limit * page;
    String order = desc == 1 ? "desc" : "asc";


    // FIXME: 2/19/18 не исользуй разные запросы!
    throw new UnsupportedOperationException("Нужно реализовать через один селект!");
  }

  @Override
  public long getClientsSize(String filter) {
    if (filter == null)
      return this.clientDao.get().countAll();
    filter = this.getFormattedFilter(filter);
    return this.clientDao.get().countByFilter(filter);
  }

  @Override
  public int remove(List<String> id) {
    return this.clientDao.get().changeClientsActuality(id, false);
  }

  @Override
  public ClientDetail detail(String id) {
    ClientDetail clientDetail = this.clientDao.get().detail(id);

    if (clientDetail != null) {
      List<ClientAddress> addresses = this.clientDao.get().getAddresses(id);
      for (ClientAddress addr : addresses) {
        if (addr.type.equals(AddressType.FACT))
          clientDetail.actualAddress = addr;
        else if (addr.type.equals(AddressType.REG))
          clientDetail.registerAddress = addr;
      }
    }

    return clientDetail;
  }

  @Override
  public void addOrUpdate(ClientToSave clientToSave) {
    if (clientToSave.id == null) {
      clientToSave.id = this.idGenerator.get().newId();
      this.clientDao.get().insertClient(clientToSave);
    } else {
      this.clientDao.get().updateClient(clientToSave);
    }

    if (clientToSave.numbersToDelete != null) {
      for (ClientPhoneNumber cpn : clientToSave.numbersToDelete) {
        this.clientDao.get().deletePhone(cpn);
      }
    }

    if (clientToSave.numersToSave != null) {
      for (ClientPhoneNumberToSave cpn : clientToSave.numersToSave) {
        if (cpn.client == null) {
          cpn.client = clientToSave.id;
          this.clientDao.get().insertPhone(cpn);
        } else {
          if (cpn.oldNumber == null)
            this.clientDao.get().insertPhone(cpn);
          else
            this.clientDao.get().updatePhone(cpn);
        }
      }
    }

    if (clientToSave.actualAddress != null) {
      if (clientToSave.actualAddress.client == null) {
        clientToSave.actualAddress.client = clientToSave.id;
        this.clientDao.get().insertAddress(clientToSave.actualAddress);
      } else {
        this.clientDao.get().updateAddress(clientToSave.actualAddress);
      }
    }
    if (clientToSave.registerAddress != null) {
      if (clientToSave.registerAddress.client == null) {
        clientToSave.registerAddress.client = clientToSave.id;
        this.clientDao.get().insertAddress(clientToSave.registerAddress);
      } else {
        this.clientDao.get().updateAddress(clientToSave.registerAddress);
      }
    }
  }

  private String getFormattedFilter(String filter) {

    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%(" + filter.toLowerCase() + "%)";
    return filter;
  }
}
