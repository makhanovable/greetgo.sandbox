package kz.greetgo.sandbox.controller.util;

import kz.greetgo.util.RND;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;

public class Util {

  public static final String floatFormat = "%.2f";
  public static final int decimalNum = 2;

  public static String floatToString(float f) {
    return String.format(Locale.US, "%f", f);
  }

  public static final String datePattern = "yyyy-MM-dd";
  public static final String reportDatePattern = "dd-MM-yyyy-hh-mm";

  public static LocalDate generateLocalDate() {
    return LocalDate.ofEpochDay(RND.plusLong(LocalDate.now().toEpochDay()));
  }

  public static Date generateDate() {
    return Date.valueOf(generateLocalDate());
  }

  public static int getAge(String stringBirthDate) {
    return getAge(LocalDate.parse(stringBirthDate));
  }

  public static int getAge(Date birthDate) {
    return getAge(birthDate.toLocalDate());
  }

  public static int getAge(LocalDate birthDate) {
    LocalDate currentDate = LocalDate.now();

    if (birthDate != null)
      return Period.between(birthDate, currentDate).getYears();
    else
      return 0;
  }

  public static long getAge(LocalDateTime birthDate) {
    LocalDateTime currentDate = LocalDateTime.now();

    if (birthDate != null)
      return ChronoUnit.YEARS.between(birthDate, currentDate);
    else
      return 0;
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

  private static final char[] ALL = (RND.eng + RND.ENG + RND.DEG).toCharArray();
  private static final Random rnd = new SecureRandom();

  public static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    int charsLength = ALL.length;
    for (int i = 0; i < length; i++) {
      sb.append(ALL[rnd.nextInt(charsLength)]);
    }

    return sb.toString();
  }
}
