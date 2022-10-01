package io.satra.iconnect.user_service.utils;

import io.satra.iconnect.user_service.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {

  private AuthenticationUtils() {
  }

  public static String getAuthenticatedUserId() {
    UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getUser().getId();
  }
}
