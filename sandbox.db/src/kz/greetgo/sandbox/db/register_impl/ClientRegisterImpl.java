package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.Arrays;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {


    String[] orders = {"age", "totalAccountBalance", "maximumBalance", "minimumBalance"};
    Boolean match = Arrays.stream(orders).anyMatch(o -> o.equals(orderBy));

    String ob = match ? orderBy : "name, surname, patronymic";
    int offset = limit * page;
    filter = getFormattedFilter(filter);
    String order = desc == 1 ? "desc" : "asc";
    return this.clientDao.get().getClients(limit, offset, filter, ob, order);
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

    // FIXME: 2/14/18 Вытаскивай одним запросом
    clientDetail.actualAddress = this.clientDao.get().getAddres(id, AddressType.FACT);
    clientDetail.registerAddress = this.clientDao.get().getAddres(id, AddressType.REG);
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
    if (filter == null)
      return "%";
    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%" + filter.toLowerCase() + "%";
    return filter;
  }
}
