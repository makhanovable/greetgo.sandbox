package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {

    throw new NotImplementedException();
  }

  @Override
  public int getClientsSize(String filter) {
    throw new NotImplementedException();
  }

  @Override
  public float remove(List<String> id) {
    throw new NotImplementedException();
  }

  @Override
  public ClientDetail detail(String id) {
    ClientDetail clientDetail = this.clientDao.get().detail(id);
    clientDetail.actualAddress = this.clientDao.get().getAddresses(id, AddressType.FACT);
    clientDetail.registerAddress = this.clientDao.get().getAddresses(id, AddressType.REG);
    return clientDetail;
  }

  @Override
  public void add(ClientToSave clientToSave) {
    throw new NotImplementedException();
  }

  @Override
  public boolean update(ClientToSave clientToSave) {
    throw new NotImplementedException();
  }
}
