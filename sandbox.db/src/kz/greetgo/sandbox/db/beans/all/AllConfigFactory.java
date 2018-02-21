package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.util.LocalConfigFactory;

@Bean
public class AllConfigFactory extends LocalConfigFactory {

  @Bean
  public DbConfig createPostgresDbConfig() {
    return createConfig(DbConfig.class);
  }

  @Bean
  public MigrationConfig createMigrationConfig() {
    return createConfig(MigrationConfig.class);
  }

}
