package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.SshConfig;
import kz.greetgo.sandbox.db.util.LocalConfigFactory;

@Bean
public class AllConfigFactory extends LocalConfigFactory {

  @Bean
  public DbConfig createPostgresDbConfig() {
    return createConfig(DbConfig.class);
  }

  @Bean
  public SshConfig createSshConfig() {
    return createConfig(SshConfig.class);
  }


}
