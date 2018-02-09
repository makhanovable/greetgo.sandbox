package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public void add(ClientForm clientForm) {

  }
  
  @Override
  public List<ClientInfo> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {
    return null;
  }

  @Override
  public int getClientsSize(String filter) {
    return 0;
  }

  @Override
  public float remove(List<Integer> id) {
    return 0;
  }

  @Override
  public ClientForm info(int id) {
    return null;
  }



  @Override
  public boolean update(ClientForm clientForm) {
    return false;
  }
}
