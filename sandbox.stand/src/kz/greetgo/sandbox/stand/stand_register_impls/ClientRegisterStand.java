package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

    public BeanGetter<StandDb> db;


    @Override
    public List<ClientInfo> getClients(int limit, int page, String filter, String orderBy, boolean orderDesk) {

        return null;
//        return new ArrayList<>(db.get().clientStorage.values());
    }

}
