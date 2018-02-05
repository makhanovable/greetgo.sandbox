package kz.greetgo.sandbox.controller.register;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientInfo;

import java.util.List;

public interface ClientRegister {

    List<ClientInfo> getClients(int limit, int page, String filter, String orderBy, boolean orderDesk);



}
