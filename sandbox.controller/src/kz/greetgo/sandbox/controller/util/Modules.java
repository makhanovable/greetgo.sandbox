package kz.greetgo.sandbox.controller.util;

import java.io.File;

@SuppressWarnings("unused")
public class Modules {
  public static File parentDir() {
    if (new File("sandbox.client").isDirectory()) {
      return new File(".");
    }

    if (new File("../sandbox.client").isDirectory()) {
      return new File("..");
    }

    throw new RuntimeException("Cannot find sandbox.parent dir");
  }

  private static File findDir(String moduleName) {
    {
      File point = new File(".");
      if (point.getAbsoluteFile().getName().equals(moduleName)) {
        return point;
      }
    }

    {
      File dir = new File(moduleName);
      if (dir.isDirectory()
        && new File("builder.gradle").isFile()
        && new File("settings.gradle").isFile()
        && new File("README.md").isFile()
        ) {
        return dir;
      }
    }

    {
      File dir = new File("../" + moduleName);
      if (dir.isDirectory()) return dir;
    }

    throw new IllegalArgumentException("Cannot find directory " + moduleName);
  }

  public static File clientDir() {
    return findDir("sandbox.client");
  }

  public static File dbDir() {
    return findDir("sandbox.db");
  }

  public static File standDir() {
    return findDir("sandbox.stand");
  }

  public static File controllerDir() {
    return findDir("sandbox.controller");
  }
}
