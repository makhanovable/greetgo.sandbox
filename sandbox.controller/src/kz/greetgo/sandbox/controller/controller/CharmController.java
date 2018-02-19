package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/charm")
public class CharmController implements Controller {

  public BeanGetter<CharmRegister> charmRegister;

  @ToJson
  @Mapping("/list")
  public List<CharmRecord> list() {
    return this.charmRegister.get().getAll();
  }

}
