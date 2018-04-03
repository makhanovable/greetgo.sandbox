package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Charm;

public class CharmDot {
    public String id;
    public String name;
    public String description;
    public Float energy;

    public Charm toCharm() {
        Charm charm = new Charm();

        charm.id = this.id;
        charm.name = this.name;
        charm.desc = this.description;
        charm.energy = this.energy;

        return charm;
    }
}
