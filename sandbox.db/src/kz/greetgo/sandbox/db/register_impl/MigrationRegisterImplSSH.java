package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MigrationRegisterImplSSH implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;


  @Override
  public void migrate() {
    throw new NotImplementedException();
  }

}
