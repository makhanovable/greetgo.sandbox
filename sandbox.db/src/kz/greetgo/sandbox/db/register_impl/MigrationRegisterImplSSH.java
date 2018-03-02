package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.migration.Migration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MigrationRegisterImplSSH implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;


  @Override
  public void migrate() {

    SshConfig sshConfig = allConfigFactory.get().createSshConfig();


    try (Migration migration = new Migration(sshConfig)) {

      migration.migrateCiaSystem();
      migration.migrateFrsSystem();

    } catch (Exception e) {

    }


    throw new NotImplementedException();
  }

}
