package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.CharmRecord;

public class CharmDot {
  public String id;
  public String name;
  public String description;
  public float energy;


  public CharmRecord toCharmInfo() {
    CharmRecord charmRecord = new CharmRecord();
    charmRecord.id = this.id;
    charmRecord.name = this.name;
    return charmRecord;
  }
}
