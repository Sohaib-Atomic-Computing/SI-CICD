package io.satra.iconnect.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FakePhoneNumber {
    private static final String testingNumberRegex = "^\\+2019\\d{8}";
    private static final String realUseNumberRegex = "^\\+2018\\d{8}";

    /**
     * Check if the number matches any fake pattern
     */
    public static Boolean isFakeNumber(String phone) {
        return isTestingNumber(phone) || isRealUseNumber(phone);
    }

    /**
     * Check if the number is a testing number
     */
    public static Boolean isTestingNumber(String phone) {
        Pattern testPhoneNumberPattern = Pattern.compile(testingNumberRegex);
        Matcher matcher = testPhoneNumberPattern.matcher(phone);
        return matcher.matches();
    }

    /**
     * Check if the number is a real-use number
     */
    public static Boolean isRealUseNumber(String phone) {
        Pattern testPhoneNumberPattern = Pattern.compile(realUseNumberRegex);
        Matcher matcher = testPhoneNumberPattern.matcher(phone);
        return matcher.matches();
    }
}
