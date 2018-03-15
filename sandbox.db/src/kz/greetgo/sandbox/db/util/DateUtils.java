package kz.greetgo.sandbox.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  public static String getDateWithTimeString(Date date) {
    return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(date);
  }

  public static String getTimeDifferenceStringFormat(Long d2, Long d1) {
    long diff = d2 - d1;

    long diffMiliSeconds = diff % 1000;
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    long diffHours = diff / (60 * 60 * 1000) % 24;
    long diffDays = diff / (24 * 60 * 60 * 1000);

    return String.format("%d days %d hours %d minutes %d seconds %d milisec", diffDays, diffHours, diffMinutes, diffSeconds, diffMiliSeconds);
  }

}
