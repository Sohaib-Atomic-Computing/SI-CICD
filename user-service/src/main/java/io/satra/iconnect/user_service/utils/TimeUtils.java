package io.satra.iconnect.user_service.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class TimeUtils {

  private TimeUtils() {
  }

  public static String getCurrentDateFormatted() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return simpleDateFormat.format(LocalDate.now());
  }
}
