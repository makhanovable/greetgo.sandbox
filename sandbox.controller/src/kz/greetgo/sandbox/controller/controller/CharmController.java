package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/charm")
public class CharmController implements Controller {

  public BeanGetter<CharmRegister> charmRegister;

  @ToJson()
  @Mapping("/dictionary")
  public List<Charm> dictionary() {
    return charmRegister.get().getCharmDictionary();
  }

  @ToJson
  @Mapping("/")
  public Charm getCharmById(@Par("charmId") int charmId) { return charmRegister.get().getCharm(charmId); }

}
