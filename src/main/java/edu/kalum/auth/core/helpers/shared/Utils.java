package edu.kalum.auth.core.helpers.shared;

import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class Utils {
    private static final DateTimeFormatter ISO_UTC_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    public String formatToIsoUTC(Date date) {
        if(date == null) {
            return null;
        }
        return ISO_UTC_FORMAT.format(date.toInstant());
    }

}
