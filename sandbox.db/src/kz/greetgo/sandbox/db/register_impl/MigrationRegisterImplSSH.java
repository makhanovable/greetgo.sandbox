package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;
import kz.greetgo.sandbox.db.configs.SshConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MigrationRegisterImplSSH implements MigrationRegister {

  public BeanGetter<AllConfigFactory> allConfigFactory;

  @Override
  public void migrate() {
    throw new NotImplementedException();
  }

  public static void main(String[] args) {
    AllConfigFactory allConfigFactory = new AllConfigFactory();
    SshConfig ssh = allConfigFactory.createSshConfig();
    String username = ssh.username();
    System.out.print(username);
  }
}
