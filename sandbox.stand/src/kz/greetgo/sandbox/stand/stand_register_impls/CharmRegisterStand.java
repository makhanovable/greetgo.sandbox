package kz.greetgo.sandbox.stand.stand_register_impls;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.InvalidCharmError;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;

import java.util.ArrayList;
import java.util.List;

@Bean
public class CharmRegisterStand implements CharmRegister {

  public BeanGetter<StandDb> db;

  @Override
  public List<Charm> getCharmDictionary() {
      List<Charm> result = new ArrayList<>();

      for (CharmDot charmDot : db.get().charmStorage.values()) {
        result.add(charmDot.toCharm());
      }

      return result;
  }

  @Override
  public Charm getCharm(int charmId) {
    CharmDot charmDot = db.get().charmStorage.get(charmId);

    if(charmDot == null) {
      throw new InvalidCharmError(404, "Invalid charm. ID:" + charmId);
    }

    return charmDot.toCharm();
  }
}
