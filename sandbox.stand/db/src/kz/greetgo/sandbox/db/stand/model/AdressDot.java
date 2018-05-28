package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientDetails;

public class AdressDot {
    public int id;
    public int clientID;
    public String adressType;
    public String street;
    public String house;
    public String flat;

    public void toClientDetails(ClientDetails clientDetails) {
        if (this.adressType.equals("FACT")) {
            clientDetails.fAdressStreet = this.street;
            clientDetails.fAdressHouse = this.house;
            clientDetails.fAdressFlat = this.flat;
        } else
        if (this.adressType.equals("REG")) {
            clientDetails.rAdressStreet = this.street;
            clientDetails.rAdressHouse = this.house;
            clientDetails.rAdressFlat = this.flat;
        }
    }
}
