package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Charm;

public class CharmDot {
  public int id;
  public String name;
  public boolean isDisabled;

  public Charm toCharm() {
    Charm ret = new Charm();

    ret.id = id;
    ret.name = name;

    return ret;
  }

  @Override
  public String toString() {
    return "CharmDot{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", isDisabled=" + isDisabled +
      '}';
  }
}
