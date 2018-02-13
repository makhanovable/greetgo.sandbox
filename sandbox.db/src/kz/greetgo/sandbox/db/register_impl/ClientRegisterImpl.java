package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;


  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {

    int offset = limit * page;
    filter = filter == null ? "" : getFormattedFilter(filter);
    String order = desc == 1 ? "desc" : "asc";
    return this.clientDao.get().getClients(limit, offset, filter, orderBy, order);
  }

  private String getFormattedFilter(String filter) {
    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%" + filter.toLowerCase() + "%";
    return filter;
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
    clientDetail.actualAddress = this.clientDao.get().getAddres(id, AddressType.FACT);
    clientDetail.registerAddress = this.clientDao.get().getAddres(id, AddressType.REG);
    return clientDetail;
  }

  @Override
  public void add(ClientToSave clientToSave) {
    clientToSave.id = this.idGenerator.get().newId();
    this.clientDao.get().insertClient(clientToSave);

    for (ClientPhoneNumber cpn : clientToSave.numersToSave) {
      cpn.client = clientToSave.id;
      this.clientDao.get().insertPhone(cpn);
    }

    if (clientToSave.actualAddress != null) {
      clientToSave.actualAddress.client = clientToSave.id;
      this.clientDao.get().insertAddress(clientToSave.actualAddress);
    }
    if (clientToSave.registerAddress != null) {
      clientToSave.registerAddress.client = clientToSave.id;
      this.clientDao.get().insertAddress(clientToSave.registerAddress);
    }

  }

  @Override
  public boolean update(ClientToSave clientToSave) {
    try {
      this.clientDao.get().updateClient(clientToSave);
      if (clientToSave.numbersToDelete != null) {
        for (ClientPhoneNumber cpn : clientToSave.numbersToDelete) {
          this.clientDao.get().deletePhone(cpn);
        }
      }

      if (clientToSave.numersToSave != null) {
        for (ClientPhoneNumberToSave cpn : clientToSave.numersToSave) {
          cpn.client = clientToSave.id;
          if (cpn.oldNumber == null)
            this.clientDao.get().insertPhone(cpn);
          else
            this.clientDao.get().updatePhone(cpn);
        }
      }

      if (clientToSave.actualAddress != null) {
        this.clientDao.get().updateAddress(clientToSave.actualAddress);
      }
      if (clientToSave.registerAddress != null) {
        this.clientDao.get().updateAddress(clientToSave.registerAddress);
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;

  }
}
