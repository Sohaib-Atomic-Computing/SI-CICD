package io.satra.iconnect.utils.sms;


import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import io.satra.iconnect.utils.PropertyLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SMSSenderCequens implements SMSSender {
    @Override
    public void sendSMS(String mobile, String message) {

        try {
            URL url = new URL(PropertyLoader.getCequensURL());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Authorization", "Bearer " + PropertyLoader.getCequensAuthKey());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("cache-control", "no-cache");

            JsonObject payload = new JsonObject();

            payload.addProperty("messageText", message);
            payload.addProperty("senderName", PropertyLoader.getCequensSenderId());
            payload.addProperty("messageType", PropertyLoader.getCequensMessageType());
            payload.addProperty("recipients", mobile);
            payload.addProperty("clientMessageId", PropertyLoader.getCequensClientMessageId());

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(payload.toString());
            wr.flush();

            //for logging purposes--->remove on production
            if (PropertyLoader.getEnv().equals("DEVELOPMENT")) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((connection.getInputStream()))
                );

                String output;
                log.info("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    log.info(output);
                }
                log.info("SMS sent successfully to " + mobile);
            }

            connection.disconnect();

        } catch (Exception e) {
            //logger.log(Level.SEVERE,e.getMessage());
            e.printStackTrace();
        }
    }
}
