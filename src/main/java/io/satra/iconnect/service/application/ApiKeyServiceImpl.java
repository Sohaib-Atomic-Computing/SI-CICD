package io.satra.iconnect.service.application;

import io.satra.iconnect.entity.ApiKey;
import io.satra.iconnect.entity.Application;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.ApiKeyRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.utils.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private ApplicationService applicationService;

    @Override
    public ApiKey createApiKey(String applicationId, String name) throws EntityNotFoundException, BadRequestException, NoSuchAlgorithmException {
        // get the application
        Application application = applicationService.getApplication(applicationId);

        // check if the api key name already exist
        apiKeyRepository.findByNameAndApplication(name, application).ifPresent(apiKey -> {
            throw new BadRequestException("The api key name already exists for the same application");
        });

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        // generate random 64 unique character api key
        String apiKey = KeyGenerator.generateAESKey(256);
        log.info("Generated api key: {}", apiKey);

        // create the api key
        ApiKey apiKeyEntity = new ApiKey();
        apiKeyEntity.setKey(apiKey);
        apiKeyEntity.setName(name);
        apiKeyEntity.setApplication(application);
        apiKeyEntity.setCreatedBy(userPrincipal.getUser());
        apiKeyEntity.setLastModifiedBy(userPrincipal.getUser());

        return apiKeyRepository.save(apiKeyEntity);
    }

    @Override
    public ApiKey updateApiKey(String apiKeyId, Boolean status, String name) throws EntityNotFoundException, BadRequestException {
        // get the api key
        ApiKey apiKey = getApiKey(apiKeyId);

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        if (name != null) {
            // check if the api key name already exist for the same application except the current api key
            apiKeyRepository.findByNameAndIdNot(name, apiKeyId).ifPresent(apiKeyEntity -> {
                throw new BadRequestException("The api key name already exists for the same application");
            });
            apiKey.setName(name);
        }
        if (status != null) {
            apiKey.setStatus(status);
        }
        apiKey.setLastModifiedBy(userPrincipal.getUser());

        return apiKeyRepository.save(apiKey);
    }

    @Override
    public void deleteApiKey(String apiKeyId) throws EntityNotFoundException {
        apiKeyRepository.delete(getApiKey(apiKeyId));
    }

    @Override
    public ApiKey getApiKey(String apiKeyId) throws EntityNotFoundException {
        return apiKeyRepository.findById(apiKeyId).orElseThrow(() -> new EntityNotFoundException("Api key not found"));
    }

    @Override
    public List<ApiKey> getAllApiKeys(String applicationId) throws EntityNotFoundException {
        // get the application
        Application application = applicationService.getApplication(applicationId);
        return apiKeyRepository.findByApplication(application);
    }

    @Override
    public ApiKey getApiKeyByKey(String key) throws BadRequestException {
        return apiKeyRepository.findByKeyAndStatusIsTrue(key).orElseThrow(() -> new BadRequestException("Invalid api key"));
    }

    @Override
    public void validateApiKey(String key) throws EntityNotFoundException, BadRequestException {
        ApiKey apiKey = getApiKeyByKey(key);

        if (apiKey == null) {
            throw new EntityNotFoundException("Api key not found");
        }

        if (!apiKey.getStatus()) {
            throw new BadRequestException("Your API Key is expired! Please contact support.");
        }

        if (!apiKey.getApplication().getStatus()) {
            throw new BadRequestException("Your Application is inactive! Please contact support.");
        }
    }
}
