package kz.greetgo.sandbox.controller.model;


public class ClientRecord {
  public String id;
  public String surname;
  public String name;
  public String patronymic;
  public int age;
  public String charm;
  public float totalAccountBalance;
  public float maximumBalance;
  public float minimumBalance;


  public String getFIO() {
    return this.name + this.surname + this.patronymic;
  }
}
