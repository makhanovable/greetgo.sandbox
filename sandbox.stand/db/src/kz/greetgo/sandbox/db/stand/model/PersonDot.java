package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.UserInfo;

public class PersonDot {
  public String id;
  public String accountName, password;
  public boolean disabled = false;
  public String surname, name, patronymic;

  public String encryptedPassword;

  //Test
  public UserInfo toUserInfo() {
    UserInfo ret = new UserInfo();
    ret.id = id;
    ret.accountName = accountName;
    ret.surname = surname;
    ret.name = name;
    ret.patronymic = patronymic;
    return ret;
  }

  public void showInfo() {
    System.out.println("----------: Init Person " + accountName + " with password " + password);
  }
}
