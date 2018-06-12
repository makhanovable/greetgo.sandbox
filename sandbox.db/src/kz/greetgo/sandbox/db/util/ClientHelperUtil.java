package kz.greetgo.sandbox.db.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class ClientHelperUtil {

    public static int calculateAge(String birth_date) {
        try {
            SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            Date birth = parserSDF.parse(birth_date);
            LocalDate birthDate = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date parseDate(String birth_date) {
        try {
            SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            Date birth = parserSDF.parse(birth_date);
            return parserSDF.parse(birth_date);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

}
