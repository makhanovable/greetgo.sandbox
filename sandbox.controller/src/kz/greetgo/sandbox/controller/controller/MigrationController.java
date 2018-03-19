package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<MigrationRegister> migrationRegister;

  @ToJson
  @NoSecurity
  @Mapping("/migrateSSH")
  public void start() throws Exception {
    this.migrationRegister.get().migrate();
  }

}
