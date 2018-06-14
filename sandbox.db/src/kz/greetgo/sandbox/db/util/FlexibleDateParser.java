package kz.greetgo.sandbox.db.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlexibleDateParser {

    private static List<String> formats = new ArrayList<String>(){{
       add("EEE MMM dd HH:mm:ss Z yyyy");
       add("yyyy-MM-dd"); // TODO add
    }};

    public static Date parseDate(String dateStr) {
        for (String format : formats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            try {
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // Ignore and try next date parser
            }
        }
        // All parsers failed
        return null;
    }
}