package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.controller.model.ClientDetails;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static kz.greetgo.sandbox.db.util.FlexibleDateParser.parseDate;

public class ClientHelperUtil {

    public static int calculateAge(String birth_date) {
        try {
            Date birth = parseDate(birth_date);
            assert birth != null;
            LocalDate birthDate = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int calculateAge(Date birth_date) {
        try {
            LocalDate birthDate = birth_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isClientDetailsValid(ClientDetails details, boolean isNew) {
        if (!isNew)
            if (details.id == null || details.id == -1)
                return false;
        return details.name != null &&
                details.surname != null &&
                details.charm != -1 &&
                details.birth_date != null &&
                details.phones != null &&
                details.phones.length != 0 &&
                details.gender != null &&
                details.addrRegStreet != null &&
                details.addrRegHome != null;
    }

    public static Map<String, Long> sortMapByValues(Map<String, Long> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

    }

}
