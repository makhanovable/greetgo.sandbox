package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientDetails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientDot {
    public String id;
    public String surname;
    public String name;
    public String patronymic;
    public String gender;
    public Date  birth_date;
    public String charmID;

    public ClientDetails toClientDetails() {
        ClientDetails clientDetails = new ClientDetails();

        clientDetails.id = this.id;
        clientDetails.name = this.name;
        clientDetails.surname = this.surname;
        clientDetails.patronymic = this.patronymic;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        clientDetails.birth_date = df.format(this.birth_date);
        clientDetails.gender = this.gender;

        return clientDetails;
    }

    public int CountAge() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = new Date();
        long diffInMillies = Math.abs(date.getTime() - birth_date.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        int years = (int) (diff / 365);


        return years;
    }
}
