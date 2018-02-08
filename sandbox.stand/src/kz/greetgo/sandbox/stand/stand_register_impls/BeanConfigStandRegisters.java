package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.BeanScanner;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.db.stand.beans.BeanConfigStandDb;

@BeanConfig
@BeanScanner
@Include({BeanConfigStandDb.class})
public class BeanConfigStandRegisters {
}
