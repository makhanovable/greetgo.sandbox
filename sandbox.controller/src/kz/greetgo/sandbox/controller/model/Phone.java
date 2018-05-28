package kz.greetgo.sandbox.controller.model;

public class Phone {
    public int clientID;
    public String number;
    public String phoneType;

    public ClientDetails toClientDetails(ClientDetails clientDetails) {
        if ("HOME".equals(this.phoneType)) {
            clientDetails.homePhone.add(this.number);
        } else
        if ("WORK".equals(this.phoneType)) {
            clientDetails.workPhone.add(this.number);
        } else {
            clientDetails.mobilePhones.add(this.number);
        }

        return clientDetails;
    }
}
