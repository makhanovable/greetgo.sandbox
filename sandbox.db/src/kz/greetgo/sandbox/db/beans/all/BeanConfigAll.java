package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.BeanScanner;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.controller.controller.BeanConfigControllers;
import kz.greetgo.sandbox.db.dao.postgres.BeanConfigPostgresDao;
import kz.greetgo.sandbox.db.migration.BeanConfigMigrationImpl;
import kz.greetgo.sandbox.db.register_impl.BeanConfigRegisterImpl;

@BeanConfig
@BeanScanner
@Include({BeanConfigRegisterImpl.class, BeanConfigPostgresDao.class, BeanConfigControllers.class,
        BeanConfigMigrationImpl.class})
public class BeanConfigAll {}
