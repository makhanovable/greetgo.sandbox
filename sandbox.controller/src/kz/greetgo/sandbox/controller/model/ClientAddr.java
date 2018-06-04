package kz.greetgo.sandbox.controller.model;

public class ClientAddr {

    public ClientAddr(int client, AddrType type, String street, String house, String flat) {
        this.client = client;
        this.type = type;
        this.street = street;
        this.house = house;
        this.flat = flat;
    }

    public int client;
    public AddrType type;
    public String street, house, flat;
}
