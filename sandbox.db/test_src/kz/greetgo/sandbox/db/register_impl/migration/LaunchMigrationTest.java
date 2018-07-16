package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

public class LaunchMigrationTest extends ParentTestNg {

    public BeanGetter<MigrationRegister> migrationRegisterBeanGetter;

    @Test
    public void launch() throws Exception{
        migrationRegisterBeanGetter.get().start();
    }

}
