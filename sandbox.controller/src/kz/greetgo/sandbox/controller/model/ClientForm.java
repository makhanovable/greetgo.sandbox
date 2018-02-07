package kz.greetgo.sandbox.controller.model;
import kz.greetgo.sandbox.controller.enums.GenderType;

import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;

public class ClientForm {
    public int id;
    public String surname;
    public String name;
    public String patronymic;
    public Date birthDate;
    public GenderType gender;
    public int charmId;
    public ClientAddress actualAddress;
    public ClientAddress registerAddress;
    public List<ClientPhoneNumber> phoneNumbers;

}
