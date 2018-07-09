package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.util.RND;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class RandomDataUtil {

    private static final String ALL = "абвгдежзийклмнопрстуфхцчшщъыьэюяАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    public static <E extends Enum<E>> E randomize(Class<E> enumClass) {
        int pick = RND.plusInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[pick];
    }

    public static Date randomDate() {
        LocalDate local = LocalDate.now().minus(Period.ofDays((RND.plusInt(365 * 110))));
        return Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

//    public static Timestamp randomTimestamp() {
//        long offset = Timestamp.valueOf("1910-01-01 00:00:00").getTime();
//        long end = System.currentTimeMillis();
//        long diff = end - offset + 1;
//        return new Timestamp(offset + (long) (Math.random() * diff));
//    }

    public static String randomStr(String... strs) {
        int pick = RND.plusInt(strs.length);
        return strs[pick];
    }

    public static String generateRndStr(int len) {
        char[] charArray = new char[len];
        for (int i = 0; i < len; i++) {
            charArray[i] = ALL.charAt(RND.plusInt(ALL.length()));
        }
        return new String(charArray);
    }

}
