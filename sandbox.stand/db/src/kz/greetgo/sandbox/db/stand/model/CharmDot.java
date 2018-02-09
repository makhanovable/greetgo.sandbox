package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.CharmInfo;

public class CharmDot {
  public String id;
  public String name;
  public String description;
  public float energy;


  public CharmInfo toCharmInfo() {
    CharmInfo charmInfo = new CharmInfo();
    charmInfo.id = this.id;
    charmInfo.name = this.name;
    return charmInfo;
  }
}
