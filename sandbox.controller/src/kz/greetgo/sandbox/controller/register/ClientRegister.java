package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClientInfoList(int limit, int page, String filter, String orderBy, int desc);

  long getClientsSize(String filter);

  int remove(List<String> id);

  ClientDetail detail(String id);

  void add(ClientToSave clientDetail);

  boolean update(ClientToSave clientDetail);
}
