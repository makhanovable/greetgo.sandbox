package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Charm;

public class CharmDot {
  public int id;
  public String name;
  public String description;
  public Float energy;

  public Charm toCharm() {
    return new Charm(this.id,this.name,this.description,this.energy);
  }
}
