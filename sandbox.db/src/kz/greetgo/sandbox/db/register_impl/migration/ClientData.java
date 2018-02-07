package kz.greetgo.sandbox.db.register_impl.migration;

public class ClientData {
  public String id;
  public String surname;

  @Override
  public String toString() {
    return "ClientData{" +
      "id='" + id + '\'' +
      ", surname='" + surname + '\'' +
      '}';
  }
}
