package io.satra.iconnect.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ValidateUtil {

    public boolean checkMobileNumberValidation(String str) {
        if(str == null || str.equals(""))
            return false;

        //Check the country code
        if(!(str.trim().startsWith("00") || str.trim().startsWith("+"))) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.WARNING, "The Phone: " + str + " is missing the country phone.");
            return false;
        }

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        boolean isValid = false;

        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(str, "EG");
            String phone = String.format("+%s%s", numberProto.getCountryCode(), numberProto.getNationalNumber());
            if (FakePhoneNumber.isFakeNumber(phone)){
                isValid =  true;
            } else {
                isValid = phoneUtil.isValidNumber(numberProto) && phoneUtil.getNumberType(numberProto) != PhoneNumberUtil.PhoneNumberType.FIXED_LINE;
            }
        } catch (NumberParseException e) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.WARNING, "NumberParseException was thrown: " + e.toString());
        }
        return isValid;
    }

    public boolean checkEmailValidation(String str) {
        if (!(str == null || str.trim().equals("") || str.trim().contains("\u0000") || str.trim().length() > 255)) {
            Pattern email = Pattern.compile(
                    "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
            if (email.matcher(str).matches()) {
                return true;
            }
        }
        return false;
    }

}
