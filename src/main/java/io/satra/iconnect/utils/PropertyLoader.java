package io.satra.iconnect.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Component
@Scope(value = "singleton")
@PropertySource(name = "config", value = "classpath:${envTarget:LOCAL}_application.properties")
public class PropertyLoader {

    // JWT VALUES
    private static String JWT_SECRET;
    private static Long JWT_EXPIRATION_MS;

    // AES VALUES
    private static String AES_SECRET;

    // ENVIRONMENT VALUES
    private static String ENV;

    // PATH VALUES
    private static String PATH_STORAGE;

    // DEFAULT USER VALUES
    private static String DEFAULT_ADMIN_EMAIL;
    private static String DEFAULT_ADMIN_PASSWORD;
    // SMS VALUES
    private static String cequensURL;
    private static String cequensAuthKey;
    private static String cequensSenderId;
    private static String cequensMessageType;
    private static String cequensClientMessageId;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static String getJwtSecret() {
        return JWT_SECRET;
    }

    @Value("${constants.jwtSecret}")
    public void setJwtSecret(String jwtSecret) {
        JWT_SECRET = jwtSecret;
    }

    public static Long getJwtExpirationMs() {
        return JWT_EXPIRATION_MS;
    }

    @Value("${constants.jwtExpirationMs}")
    public void setJwtExpirationMs(Long jwtExpirationMs) {
        JWT_EXPIRATION_MS = jwtExpirationMs;
    }

    public static String getAesSecret() {
        return AES_SECRET;
    }

    @Value("${constants.default.aes.password}")
    public void setAesSecret(String aesSecret) {
        AES_SECRET = aesSecret;
    }

    public static String getEnv() {
        return ENV;
    }

    @Value("${constants.env}")
    public void setEnv(String envTarget) {
        ENV = envTarget;
    }

    public static String getPathStorage() {
        return PATH_STORAGE;
    }

    @Value("${constants.path.storage}")
    public void setPathStorage(String pathStorage) {
        PATH_STORAGE = pathStorage;
    }

    public static String getDefaultAdminEmail() {
        return DEFAULT_ADMIN_EMAIL;
    }

    @Value("${constants.default.admin.email}")
    public void setDefaultAdminEmail(String defaultAdminEmail) {
        DEFAULT_ADMIN_EMAIL = defaultAdminEmail;
    }

    public static String getDefaultAdminPassword() {
        return DEFAULT_ADMIN_PASSWORD;
    }

    @Value("${constants.default.admin.password}")
    public void setDefaultAdminPassword(String defaultAdminPassword) {
        DEFAULT_ADMIN_PASSWORD = defaultAdminPassword;
    }

    public static String getCequensURL() {
        return cequensURL;
    }

    @Value("${constants.cequens.url}")
    public void setCequensURL(String cequensURL) {
        PropertyLoader.cequensURL = cequensURL;
    }

    public static String getCequensAuthKey() {
        return cequensAuthKey;
    }

    @Value("${constants.cequens.authKey}")
    public void setCequensAuthKey(String cequensAuthKey) {
        PropertyLoader.cequensAuthKey = cequensAuthKey;
    }

    public static String getCequensSenderId() {
        return cequensSenderId;
    }

    //setter
    @Value("${constants.cequens.senderId}")
    public void setCequensSenderId(String cequensSenderId) {
        PropertyLoader.cequensSenderId = cequensSenderId;
    }

    public static String getCequensMessageType() {
        return cequensMessageType;
    }

    @Value("${constants.cequens.messageType}")
    public void setCequensMessageType(String cequensMessageType) {
        PropertyLoader.cequensMessageType = cequensMessageType;
    }

    public static String getCequensClientMessageId() {
        return cequensClientMessageId;
    }

    @Value("${constants.cequens.clientMessageId}")
    public void setCequensClientMessageId(String cequensClientMessageId) {
        PropertyLoader.cequensClientMessageId = cequensClientMessageId;
    }

}
