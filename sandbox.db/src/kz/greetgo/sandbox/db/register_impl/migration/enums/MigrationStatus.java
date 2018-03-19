package kz.greetgo.sandbox.db.register_impl.migration;

@SuppressWarnings("WeakerAccess")
public class MigrationStatus {

  public static int NOT_READY = 1;
  public static int TO_INSERT = 2;
  public static int TO_UPDATE = 3;
  public static int LAST_ACTUAL = 4;

}
