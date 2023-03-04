package io.satra.iconnect.service.application;

import io.satra.iconnect.entity.Application;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.ApplicationRepository;
import io.satra.iconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Application createApplication(String name) throws BadRequestException, EntityNotFoundException {
        // check if application exists
        if(applicationRepository.findByName(name).isPresent()){
            throw new BadRequestException("Application already exists with the name: " + name);
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        // create the application
        Application application = new Application();
        application.setName(name);
        application.setCreatedBy(userPrincipal.getUser());
        application.setLastModifiedBy(userPrincipal.getUser());

        return applicationRepository.save(application);
    }

    @Override
    public Application updateApplication(String id, Boolean isActive, String name) throws EntityNotFoundException {
        // check if application exists
        Application application = getApplication(id);

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getUser() == null) {
            throw new EntityNotFoundException("User not found");
        }

        if (name != null) {
            // check if the name is already taken by another application except the current one
            if(applicationRepository.existsByNameAndIdNotAndIsActiveTrue(name, id)){
                throw new BadRequestException("Application already exists with the name: " + name);
            }
            application.setName(name);
        }
        if (isActive != null) {
            application.setIsActive(isActive);
        }
        application.setLastModifiedBy(userPrincipal.getUser());

        return applicationRepository.save(application);
    }

    @Override
    public void deleteApplication(String id) throws EntityNotFoundException {
        // apply soft delete
        updateApplication(id, false, null);
    }

    @Override
    public Application getApplication(String id) throws EntityNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Application not found"));
    }

    @Override
    public Page<Application> getAllApplications(String name, Boolean isActive, Pageable page) {
        if (isActive == null) isActive = true;
        Specification<Application> specification = ApplicationSpecifications.filterApplications(name, isActive);
        return applicationRepository.findAll(specification, page);
    }
}
