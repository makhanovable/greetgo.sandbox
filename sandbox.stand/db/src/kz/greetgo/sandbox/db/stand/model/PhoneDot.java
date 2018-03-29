package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.PhoneType;

public class PhoneDot {
    public String clientID;
    public String number;
    public String phoneType;

    public void toClientDetails(ClientDetails clientDetails) {
        if (this.phoneType.equals("HOME")) {
            clientDetails.homePhone.add(this.number);
        } else
        if (this.phoneType.equals("WORK")) {
            clientDetails.workPhone.add(this.number);
        } else {
            clientDetails.mobilePhones.add(this.number);
        }
    }
}
