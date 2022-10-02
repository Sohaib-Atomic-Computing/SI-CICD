package io.satra.iconnect.user_service.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtils {

  private TimeUtils() {
  }

  public static String getCurrentDateFormatted() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return simpleDateFormat.format(LocalDate.now());
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
