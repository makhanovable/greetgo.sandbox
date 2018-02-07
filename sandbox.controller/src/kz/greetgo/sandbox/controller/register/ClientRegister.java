package kz.greetgo.sandbox.controller.register;
import com.sun.org.apache.xpath.internal.operations.Bool;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;

import java.util.List;

public interface ClientRegister {

    List<ClientInfo> getClientInfoList(int limit, int page, String filter, String orderBy, int desc);

    int getClientsSize(String filter);

    float remove(List<Integer> id);

    ClientForm info(int id);

    void add(ClientForm clientForm);

    boolean update(ClientForm clientForm);
}
