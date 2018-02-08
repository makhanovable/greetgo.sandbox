package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.CharmInfo;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;

import java.util.ArrayList;
import java.util.List;

@Bean
public class CharmRegisterStand implements CharmRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<StandDb> db;

  @Override
  public List<CharmInfo> getAll() {
    List<CharmInfo> list = new ArrayList<>();

    for (CharmDot charmDot : db.get().charmStorage.values()) {
      list.add(charmDot.toCharmInfo());
    }
    return list;
  }
}
