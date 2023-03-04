package io.satra.iconnect.service.application;

import io.satra.iconnect.entity.Application;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ApplicationService {

    /**
     * This method is used to add a new application
     *
     * @param name the application name.
     * @return the created application {@link Application}
     * @throws BadRequestException if the application already exists
     * @throws EntityNotFoundException if the user does not exists
     * */
    Application createApplication(String name) throws BadRequestException, EntityNotFoundException;

    /**
     * This method is used to update an application
     *
     * @param id the id of the application to be updated
     * @param name the application name to update
     * @param isActive the application isActive status to update
     * @return the updated application {@link Application}
     * @throws EntityNotFoundException if the application not exists
     * */
    Application updateApplication(String id, Boolean isActive, String name) throws EntityNotFoundException;

    /**
     * This method is used to delete an application
     *
     * @param id the id of the application to be deleted
     * @throws EntityNotFoundException if the application does not exists
     * */
    void deleteApplication(String id) throws EntityNotFoundException;

    /**
     * This method is used to get an application by given id
     *
     * @param id the is of the application to be obtained
     * @return the application data {@link Application}
     * @throws EntityNotFoundException if the application does not exist
     * */
    Application getApplication(String id) throws EntityNotFoundException;

    /**
     * This method is used to get all applications
     *
     * @param name the application name to be obtained (Optional)
     * @param status the application status to be obtained (Optional)
     * @param page the pagination information
     * @return the list of applications {@link Application}
     * */
    Page<Application> getAllApplications(String name, Boolean status, Pageable page);

}
