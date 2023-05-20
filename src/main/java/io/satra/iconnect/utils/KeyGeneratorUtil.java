package io.satra.iconnect.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class is used to generate a random key for the application.
 *
 * @author Shehab Salah
 * @version 1.0
 * @since 2023-01-18
 */
public class KeyGeneratorUtil {
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<=>?@_!#%&()*+-~";
    public static final int SECURE_TOKEN_LENGTH = 16;

    private static final SecureRandom random = new SecureRandom();

    private static final char[] symbols = CHARACTERS.toCharArray();

    private static final char[] buf = new char[SECURE_TOKEN_LENGTH];

    /**
     * Generate the next secure random key in the series.
     * Generate key with default length 16 characters
     * Characters are: a-z, A-Z, 0-9, <=>?@_!#%&()*+-~
     *
     * @return String key, the generated string key which is 16 characters long.
     */
    public static String nextKey() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * Generate the next secure random key in the series.
     * Generate key with length of the given parameter.
     * Characters are: a-z, A-Z, 0-9, <=>?@_!#%&()*+-~
     *
     * @param length of the key. Which mean the key will be generated with the given length.
     *               for example: 16 will generate key with 16 characters.
     *               Note: the length must be greater than 0. If not, the key will be generated with default length 16 characters.
     *               Note: the length must be less than the characters length. If not, the key will be generated with the characters length.
     *               for example: if the characters are "abc" and the length is 16, the key will be generated with 3 characters.
     * @return String key, String key, the generated string key which is the given length long.
     * */
    public static String nextKey(int length) {
        if (length <= 0) {
            return nextKey();
        }

        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * Generate the next secure random key in the series.
     * Generate key with length of the given parameter and characters of the given parameter.
     *
     * @param length of the key. Which mean the key will be generated with the given length.
     *               for example: 16 will generate key with 16 characters.
     *               Note: the length must be greater than 0. If not, the key will be generated with default length 16 characters.
     *               Note: the length must be less than the characters length. If not, the key will be generated with the characters length.
     *               for example: if the characters are "abc" and the length is 16, the key will be generated with 3 characters.
     * @param characters of the key. Which mean the key will be generated from these characters.
     *                   Parameter value sample: "abc123"
     *                   for example: "abc" will generate key from a, b, c only.
     *                   "abc123" will generate key from a, b, c, 1, 2, 3 only.
     *                   Note: the characters must be unique. If not, the key will be generated from the unique characters only.
     *                   for example: "abcabc" will generate key from a, b, c only.
     *                   "abc123abc123" will generate key from a, b, c, 1, 2, 3 only.
     *
     * @return String key, the generated string key which is the given length long.
     * */
    public static String nextKey(int length, String characters) {
        if (length <= 0) {
            length = SECURE_TOKEN_LENGTH;
        }

        char[] buf = new char[length];
        char[] symbols = characters.toCharArray();
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);

    }

    /**
     * Using AES algorithm to generate a random key.
     *
     * @param keyLength the length of the key in bits.
     *                  For example: 128, 192, 256
     *                  Note: the key length must be one of these values: 128, 192, 256
     *                  If not, the key will be generated with default value 128.
     *
     * @return String the generated string key.
     * */
    public static String generateKeyWithAES(final int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
        keyGen.init(keyLength);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        // convert to hex
        return bytesToHex(encoded).toLowerCase();
    }

    private static String bytesToHex(byte[] encoded) {
        StringBuilder sb = new StringBuilder();
        for (byte b : encoded) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
