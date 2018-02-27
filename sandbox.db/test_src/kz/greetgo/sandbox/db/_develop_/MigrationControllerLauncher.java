package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

import java.io.File;
import java.io.FileInputStream;

public class MigrationControllerLauncher {
  public static void main(String[] args) throws Exception {
    new MigrationControllerLauncher().run();
  }

  private void run() throws Exception {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    String ciaFilename = null;
    String ciaFilepath = "" + ciaFilename + ".xml";
    File errorFile = new File(ciaFilename + ".error.txt");
    File reportFile = new File(ciaFilename + ".ods");

    bc.migrationController()
      .migrateOneCiaFile(new FileInputStream(new File(ciaFilepath)), ciaFilename, errorFile, reportFile);
  }
}
