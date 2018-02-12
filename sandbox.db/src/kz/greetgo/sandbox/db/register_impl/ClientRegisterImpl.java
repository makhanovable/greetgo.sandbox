package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ClientRegisterImpl implements ClientRegister {

  @Override
  public List<ClientInfo> getClientInfoList(int limit, int page, String filter, String orderBy, int desc) {

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
  public ClientForm info(String id) {
    throw new NotImplementedException();
  }

  @Override
  public void add(ClientForm clientForm) {
    throw new NotImplementedException();
  }

  @Override
  public boolean update(ClientForm clientForm) {
    throw new NotImplementedException();
  }
}
