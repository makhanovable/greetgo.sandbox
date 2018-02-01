package kz.greetgo.sandbox.controller.util;

import kz.greetgo.util.RND;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Locale;

public class Util {

  public static final String floatFormat = "%.2f";

  public static String floatToString(float f) {
    return String.format(Locale.US, "%f", f);
  }

  public static float stringToFloat(String s) {
    return Float.parseFloat(s);
  }

  public static final String datePattern = "YYYY-MM-DD";
  public static final String reportDatePattern = "dd-MM-yyyy-hh-mm";

  public static LocalDate generateLocalDate() {
    return LocalDate.ofEpochDay(RND.plusLong(LocalDate.now().toEpochDay()));
  }

  public static Date generateDate() {
    return Date.valueOf(generateLocalDate());
  }

  public static String getFullname(String surname, String name, String patronymic) {
    StringBuilder b = new StringBuilder();

    if (!surname.isEmpty()) {
      b.append(surname);
      b.append(" ");
    }
    if (!name.isEmpty()) {
      b.append(name);
      b.append(" ");
    }
    if (!patronymic.isEmpty())
      b.append(patronymic);

    return b.toString().trim();
  }
}
