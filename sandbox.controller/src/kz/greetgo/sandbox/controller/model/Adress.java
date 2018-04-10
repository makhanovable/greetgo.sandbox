package kz.greetgo.sandbox.controller.model;

public class Adress {
    public int id;
    public int clientID;
    public String adressType;
    public String street;
    public String house;
    public String flat;

    public ClientDetails toClientDetails(ClientDetails clientDetails) {
        if ("FACT".equals(this.adressType)) {
            clientDetails.fAdressStreet = this.street;
            clientDetails.fAdressHouse = this.house;
            clientDetails.fAdressFlat = this.flat;
        } else
        if ("REG".equals(this.adressType)) {
            clientDetails.rAdressStreet = this.street;
            clientDetails.rAdressHouse = this.house;
            clientDetails.rAdressFlat = this.flat;
        }

        return clientDetails;
    }
}
