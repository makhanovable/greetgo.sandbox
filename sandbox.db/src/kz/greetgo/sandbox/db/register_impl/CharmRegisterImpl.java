package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.CharmInfo;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.db.dao.CharmDao;

import java.util.List;

@Bean
public class CharmRegisterImpl implements CharmRegister {

  public BeanGetter<CharmDao> charmDao;

  @Override
  public List<CharmInfo> getAll() {
    return this.charmDao.get().getAll();
  }
}
