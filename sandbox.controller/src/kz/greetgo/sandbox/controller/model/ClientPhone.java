package kz.greetgo.sandbox.controller.model;

public class ClientPhone {
    public int client;
    public String number;
    public PhoneType type;

    public ClientPhone(int client, String number, PhoneType type) {
        this.client = client;
        this.number = number;
        this.type = type;
    }
}
