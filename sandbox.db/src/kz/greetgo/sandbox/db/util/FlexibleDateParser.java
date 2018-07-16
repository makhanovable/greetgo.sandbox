package kz.greetgo.sandbox.db.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlexibleDateParser {

    private static Logger logger = Logger.getLogger(FlexibleDateParser.class);

    private static List<String> formats = new ArrayList<String>(){{
       add("EEE MMM dd HH:mm:ss Z yyyy");
       add("yyyy-MM-dd");
    }};

    public static Date parseDate(String dateStr) {
        for (String format : formats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            try {
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // Ignore and try next date parser
                logger.error(e);
            }
        }
        // All parsers failed
        return null;
    }
}