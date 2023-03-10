package io.satra.iconnect.service.promotion;

import io.satra.iconnect.dto.PromotionDTO;
import io.satra.iconnect.dto.scandto.ScannerMessageDTO;
import io.satra.iconnect.dto.request.PromotionRequestDTO;
import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface PromotionService {

    /**
     * This method is used to add a new promotion
     *
     * @param promotionRequestDTO the promotion information to register
     * @return the registered promotion {@link PromotionDTO}
     * @throws BadRequestException if the promotion already exists
     * @throws EntityNotFoundException if the user does not exist
     */
    PromotionDTO createPromotion(PromotionRequestDTO promotionRequestDTO) throws BadRequestException, EntityNotFoundException;

    /**
     * This method is used to update a promotion
     *
     * @param id the id of the promotion to be updated
     * @param promotionRequestDTO the promotion information to update
     * @return the updated promotion {@link PromotionDTO}
     * @throws EntityNotFoundException if the promotion already exists
     */
    PromotionDTO updatePromotion(String id, PromotionRequestDTO promotionRequestDTO) throws EntityNotFoundException;

    /**
     * This method is used to delete a promotion
     *
     * @param id the id of the promotion to be deleted
     * @throws EntityNotFoundException if the promotion does not exist
     */
    void deletePromotion(String id) throws EntityNotFoundException;

    /**
     * This method is used to get a promotion by given id
     *
     * @param id the id of the promotion to be obtained
     * @return the promotion data {@link PromotionDTO}
     * @throws EntityNotFoundException if the promotion does not exist
     */
    PromotionDTO getPromotion(String id) throws EntityNotFoundException;

    /**
     * This method is used to get all promotions
     *
     * @param name the name of the promotion to be obtained
     * @param isActive the active status of the promotion to be obtained
     * @param startDateFrom the start date from of the promotion to be obtained
     * @param startDateTo the start date to of the promotion to be obtained
     * @param endDateFrom the end date from of the promotion to be obtained
     * @param endDateTo the end date to of the promotion to be obtained
     * @param page the pagination information
     * @return the list of promotions {@link PromotionDTO}
     */
    Page<PromotionDTO> getAllPromotions(String name, Boolean isActive, String startDateFrom, String startDateTo,
                                        String endDateFrom, String endDateTo, Pageable page);

    /**
     * This method is used to scan a promotion
     *
     * @param scanDTO the scan information
     * @throws EntityNotFoundException if the promotion does not exist
     */
    List<PromotionDTO> promotionScannerValidator(ScanDTO scanDTO) throws EntityNotFoundException;

    /**
     * This method is used to get all vendor promotions
     *
     * @param vendorId the vendor id to get promotions for
     * @return the list of promotions {@link PromotionDTO}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    List<PromotionDTO> getVendorPromotions(String vendorId) throws EntityNotFoundException;

    /**
     * This method is used encrypt the user id using AES
     *
     * @param scannerMessageDTO the user id information {@link ScannerMessageDTO}
     * @return the encrypted user id
     * @throws BadRequestException if the user id is not valid
     */
    HashMap<String, Object> encrypt(ScannerMessageDTO scannerMessageDTO) throws BadRequestException;
}
