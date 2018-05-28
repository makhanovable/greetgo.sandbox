package kz.greetgo.sandbox.controller.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Client {
    public int id;
    public String surname;
    public String name;
    public String patronymic;
    public String gender;
    public Date birth_date;
    public int charm_id;

    public ClientDetails toClientDetails() {
        ClientDetails clientDetails = new ClientDetails();

        clientDetails.id = this.id;
        clientDetails.name = this.name;
        clientDetails.surname = this.surname;
        clientDetails.patronymic = this.patronymic;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        clientDetails.birth_date = df.format(this.birth_date);
        clientDetails.gender = this.gender;
        clientDetails.charm_id = this.charm_id;

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
