package io.satra.iconnect.user_service.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {

  private TimeUtils() {
  }

  public static String getCurrentDateFormatted() {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
  }

  public static LocalDateTime convertDateToLocalDateTime(Date date) {
    return date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
    return java.util.Date
        .from(localDateTime.atZone(ZoneId.systemDefault())
            .toInstant());
  }
}
