package kz.greetgo.sandbox.db.stand.tools;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class AgeCalculator {
  public static int calculateAge(Date birth, Date current) {
    LocalDate birthDate = asLocalDate(birth);
    LocalDate currentDate = asLocalDate(current);
    if ((birthDate != null) && (currentDate != null)) {
      return Period.between(birthDate, currentDate).getYears();
    } else {
      return 0;
    }
  }

  private static LocalDate asLocalDate(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }
}
