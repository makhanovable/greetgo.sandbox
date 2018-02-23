package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

import java.util.concurrent.TimeUnit;

public class MigrationManagerLauncher {
  public static void main(String[] args) throws Exception {
    new MigrationManagerLauncher().run();
  }

  private void run() throws Exception {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    long init = System.currentTimeMillis();

    boolean keep = true;
    while (keep) {
      keep = false;
      if (bc.migrationManager().connectAndMigrateOneCiaFile())
        keep = true;

      if (bc.migrationManager().connectAndMigrateOneFrsFile())
        keep = true;

      System.out.println("Произведена итерация");

      Thread.sleep(1000);
    }

    long post = System.currentTimeMillis();
    System.out.println("Затраченное время " + TimeUnit.MILLISECONDS.toSeconds(post - init));
  }
}
