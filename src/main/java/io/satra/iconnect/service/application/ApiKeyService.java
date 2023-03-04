package io.satra.iconnect.service.application;

import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public interface ApiKeyService {

    /**
     * This used to add a new api key to the application
     *
     * @param applicationId the application id
     * @param name the api key name
     * @return the created api key {@link ApiKey}
     * @throws EntityNotFoundException if the application does not exist
     * @throws BadRequestException if the api key name already exists for the same application
     * */
    ApiKey createApiKey(String applicationId, String name) throws EntityNotFoundException, BadRequestException, NoSuchAlgorithmException;

    /**
     * This used to update an api key
     *
     * @param apiKeyId the api key id
     * @param name the api key name
     * @param isActive the api key active status
     * @return the updated api key {@link ApiKey}
     * @throws EntityNotFoundException if the api key does not exist
     * @throws BadRequestException if the api key name already exists for the same application
     * */
    ApiKey updateApiKey(String apiKeyId, Boolean isActive, String name) throws EntityNotFoundException, BadRequestException;

    /**
     * This used to delete an api key
     *
     * @param apiKeyId the api key id
     * @throws EntityNotFoundException if the api key does not exist
     * */
    void deleteApiKey(String apiKeyId) throws EntityNotFoundException;

    /**
     * This used to get an api key by given id
     *
     * @param apiKeyId the api key id
     * @return the api key data {@link ApiKey}
     * @throws EntityNotFoundException if the api key does not exist
     * */
    ApiKey getApiKey(String apiKeyId) throws EntityNotFoundException;

    /**
     * This used to get all api keys for the given application
     *
     * @param applicationId the application id
     * @return the list of api keys {@link ApiKey}
     * @throws EntityNotFoundException if the application does not exist
     * */
    List<ApiKey> getAllApiKeys(String applicationId) throws EntityNotFoundException;

    /**
     * This method is used to get API key by given key and validate if it is active
     *
     * @param key the api key
     * @return the api key data {@link ApiKey}
     * @throws BadRequestException if the api key does not exist or it is not active
     * */
    ApiKey getApiKeyByKey(String key) throws BadRequestException;

    /**
     * This method is used to validate the API key and make sure that the API key is exists and valid.
     *
     * @param key the key to validate
     * @throws EntityNotFoundException if the API key is null
     * @throws BadRequestException is the API key expired
     * */
    void validateApiKey(String key) throws EntityNotFoundException, BadRequestException;
}
