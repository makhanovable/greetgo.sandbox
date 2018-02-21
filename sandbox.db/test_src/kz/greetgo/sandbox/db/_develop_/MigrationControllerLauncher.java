package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

public class MigrationControllerLauncher {
  public static void main(String[] args) throws Exception {
    new MigrationControllerLauncher().run();
  }

  private void run() throws Exception {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    bc.migrationController().migrate();
  }
}
