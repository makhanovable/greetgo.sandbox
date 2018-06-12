package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.util.RND;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class RandomDataUtil {

    public static <E extends Enum<E>> E randomize(Class<E> enumClass) {
        int pick = RND.plusInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[pick];
    }

    public static Date randomDate() {
        LocalDate local = LocalDate.now().minus(Period.ofDays((RND.plusInt(365 * 110))));
        return Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Timestamp randomTimestamp() {
        long offset = Timestamp.valueOf("1910-01-01 00:00:00").getTime();
        long end = System.currentTimeMillis();
        long diff = end - offset + 1;
        return new Timestamp(offset + (long) (Math.random() * diff));
    }

    public static String randomStr(String... strs) {
        int pick = RND.plusInt(strs.length);
        return strs[pick];
    }

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

}
