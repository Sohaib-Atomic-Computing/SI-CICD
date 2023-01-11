package io.satra.iconnect.utils.sms;

public interface SMSSender {
    void sendSMS(String mobile, String message);
}
