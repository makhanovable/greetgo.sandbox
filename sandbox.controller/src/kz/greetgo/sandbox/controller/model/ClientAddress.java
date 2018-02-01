package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.AddressType;

/**
 * Created by damze on 2/1/18.
 */
public class ClientAddress {
    private int clientId;
    private AddressType type;
    private String street;
    private String house;
    private String flat;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }
}
