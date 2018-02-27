package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.register_impl.migration.MigrationManager;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class MigrationManagerLauncher {

  Logger logger = Logger.getLogger(MigrationManagerLauncher.class);

  public static void main(String[] args) throws Exception {
    new MigrationManagerLauncher().run();
  }

  private void run() throws Exception {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    logger.info("Начало теста системы миграции");

    long init = System.currentTimeMillis();

    boolean keep;
    do {
      keep = false;

      if (bc.migrationManager().connectAndMigrateOneFile(MigrationManager.eFiletype.CIA))
        keep = true;
      if (bc.migrationManager().connectAndMigrateOneFile(MigrationManager.eFiletype.FRS))
        keep = true;

      logger.info("Произведена итерация");
      Thread.sleep(100);
    } while (keep);

    long post = System.currentTimeMillis();
    logger.info("Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init));

    System.exit(0);
  }
}
