package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.PhoneNumberType;

/**
 * Created by damze on 2/1/18.
 */
public class ClientPhone {
    private int clientId;
    private String number;
    private PhoneNumberType type;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneNumberType getType() {
        return type;
    }

    public void setType(PhoneNumberType type) {
        this.type = type;
    }
}
