package kz.greetgo.sandbox.db.stand.model;

import sun.jvm.hotspot.utilities.Interval;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Client {
    public String id;
    public String surname;
    public String name;
    public String patronymic;
    public String gender;
    public Date  birth_date;
    public String charmID;

    public int CountAge() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = new Date();
//        Interval interval = new Interval(date, this.birth_date);
//        System.out.println(interval.toString());
        long diffInMillies = Math.abs(date.getTime() - birth_date.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        int years = (int) (diff / 365);


        return years;
    }
}
