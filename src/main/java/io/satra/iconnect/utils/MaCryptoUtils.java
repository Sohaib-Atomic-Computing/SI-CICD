package io.satra.iconnect.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Component
@Slf4j
public class MaCryptoUtils {

    public static void main(String[] args) {
        MaCryptoUtils mu = new MaCryptoUtils();
        System.out.println(mu.encryptAES("Lqvc0G6JFP@", "78666"));
    }

    private SecretKeySpec setKey(String plainkey) {
        SecretKeySpec secretKey = null;
        byte[] keybytes;

        MessageDigest sha = null;
        try {
            keybytes = plainkey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            keybytes = sha.digest(keybytes);
            keybytes = Arrays.copyOf(keybytes, 16);
            secretKey = new SecretKeySpec(keybytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public String encryptAES(String plaintext, String password) {

        SecretKeySpec aeskey = setKey(password);

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, aeskey);

            String ciphertext = Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
            return ciphertext;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptAES(String ciphertext, String password) {

        SecretKeySpec aeskey = setKey(password);
        try {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, aeskey);

            String plaintext = new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
            return plaintext;

        } catch (Exception e) {
            log.error("Error decrypting: {}", e.getMessage());
        }
        return null;
    }

    public String encryptAESURL(String plaintext) {
        try {
            byte[] keyBytes = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes());
//            System.out.println("encrypted token size:" + cipherText.length);
            //Encode Character which are not allowed on URL
            String encodedTxt = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(cipherText);

//            System.out.println("EncodedEncryptedToken : " + encodedTxt);
            return encodedTxt;
        } catch (Exception e) {

        }
        return null;
    }

    public String decryptAESURL(String text) {
        try {
            byte[] keyBytes = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.DECRYPT_MODE, key);
            String decodeStr = URLDecoder.decode(
                    text,
                    StandardCharsets.UTF_8.toString());
//            System.out.println("URL Decoder String :" + decodeStr);
            //Decode URl safe to base 64
            byte[] base64decodedTokenArr = org.apache.commons.codec.binary.Base64.decodeBase64(decodeStr.getBytes());

            byte[] decryptedPassword = cipher.doFinal(base64decodedTokenArr);
            //byte[] decryptedPassword = cipher.doFinal(decodeStr.getBytes());
            String decodeTxt = new String(decryptedPassword);
//            System.out.println("Token after decryption: " + decodeTxt);
            return decodeTxt;
        } catch (Exception e) {

        }

        return null;
    }

    public String encryptAES2AndTrunc(String plaintext, String password) {

        SecretKeySpec aeskey = setKey(password);

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, aeskey);

            String ciphertext = Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes()));
            return ciphertext.substring(0, 15);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
