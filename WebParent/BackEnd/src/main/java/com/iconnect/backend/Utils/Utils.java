/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iconnect.backend.Utils;

import com.iconnect.backend.security.services.UserPrinciple;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author Waqar
 */
@Component
public class Utils {
    
   
    
    public static String getCurrentTimeStamp() {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
    return strDate;
}
    
    public static String getCurrentTimeStampwithtime() {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
    return strDate;
    }

    public static String getYearTimeStampMMYY(int year) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/YYYY");//dd/MM/yyyy
        date.setTime(new Date());
        date.add(Calendar.YEAR,year);
        String strDate = sdfDate.format(date.getTime());
        return strDate;
    }
    
   
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    long diffInMillies = date2.getTime() - date1.getTime();
    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
}
    
    public static int  getPasswordCharaters (String password)      
    {
        int numOfSpecial = 0;
        int numOfLetters = 0;
        int numOfDigits = 0;
        int totalCharacter = 0;

        byte[] bytes = password.getBytes();
        for (byte tempByte : bytes) {
            if (tempByte >= 33 && tempByte <= 47) {
                numOfSpecial++;
                totalCharacter++;
            }

            char tempChar = (char) tempByte;
            if (Character.isDigit(tempChar)) {
                numOfDigits++;
                totalCharacter++;
            }

            if (Character.isLetter(tempChar)) {
                numOfLetters++;
                totalCharacter++;
            }

        }
        return totalCharacter;
    }
    
 public static UUID generateType4UUID() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }
 
 
 
public static String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    String specialChar = RandomStringUtils.random(2, 35, 43, false, false);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
      .concat(numbers)
      .concat(specialChar)
      .concat(totalChars);
    List<Character> pwdChars = combinedChars.chars()
      .mapToObj(c -> (char) c)
      .collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
      .toString();
    return password;
}

 public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }


    public static String encodeBase64(final String clearText) throws NoSuchAlgorithmException {
        return new String(
                Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(clearText.getBytes(StandardCharsets.UTF_8))));
    }

  public static Long getUserId() {
         UserPrinciple userDetails = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Long userId = userDetails.getId();
        
        return userId;
    }
}
