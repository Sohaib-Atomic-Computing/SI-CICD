package io.satra.iconnect.user_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncodingUtils {

  private EncodingUtils() {
  }

  public static String encodeBase64(final String clearText) throws NoSuchAlgorithmException {
    return Arrays.toString(
        Base64.getEncoder().encode(MessageDigest
            .getInstance("SHA-256")
            .digest(clearText.getBytes(StandardCharsets.UTF_8))
        )
    );
  }
}
