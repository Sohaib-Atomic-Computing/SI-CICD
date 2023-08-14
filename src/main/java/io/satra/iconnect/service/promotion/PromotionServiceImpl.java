package io.satra.iconnect.service.promotion;

import com.google.gson.Gson;
import io.satra.iconnect.dto.PromotionDTO;
import io.satra.iconnect.dto.scandto.ScannerMessageDTO;
import io.satra.iconnect.dto.request.PromotionRequestDTO;
import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.entity.*;
import io.satra.iconnect.entity.enums.PromotionStatus;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.PromotionRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.service.user.UserService;
import io.satra.iconnect.service.validator.ValidatorService;
import io.satra.iconnect.service.vendor.VendorService;
import io.satra.iconnect.utils.MaCryptoUtils;
import io.satra.iconnect.utils.PropertyLoader;
import io.satra.iconnect.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final VendorService vendorService;
    private final ValidatorService validatorService;
    private final UserService userService;

    /**
     * This method is used to add a new promotion
     *
     * @param promotionRequestDTO the promotion information to register
     * @return the registered promotion {@link PromotionDTO}
     * @throws BadRequestException if the promotion already exists
     */
    @Override
    public PromotionDTO createPromotion(PromotionRequestDTO promotionRequestDTO) throws BadRequestException, EntityNotFoundException {
        // check if promotion request is valid
        // check if the vendor exists
        Vendor vendor = vendorService.findVendorEntityById(promotionRequestDTO.getVendorId());

        // check if the promotion already exists
        if (promotionRepository.findByVendorAndName(vendor, promotionRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Promotion already exists");
        }

        // load the users from the request users ids
        List<User> users = userService.findUsersByIds(promotionRequestDTO.getUserIds());

        // convert list of users to set of users
        Set<User> usersSet = new HashSet<>(users);

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = userPrincipal.getMerchant();
//        if (userPrincipal.getMerchant() == null) {
//            throw new EntityNotFoundException("Merchant not found");
//        }

        // create the promotion entity
        Promotion promotion = Promotion.builder()
                .name(promotionRequestDTO.getName())
                .description(promotionRequestDTO.getDescription())
                .startDate(TimeUtils.convertStringToLocalDateTime(promotionRequestDTO.getStartDate()))
                .endDate(TimeUtils.convertStringToLocalDateTime(promotionRequestDTO.getEndDate()))
                .vendor(vendor)
                .users((usersSet))
                // TODO: change the status to back to pending
                .status(PromotionStatus.APPROVED)
                .merchant(merchant)
                .build();

        if (promotionRequestDTO.getIsActive() != null) {
            promotion.setIsActive(promotionRequestDTO.getIsActive());
        }

        // save the promotion
        promotion = promotionRepository.save(promotion);

        return promotion.toDTO();
    }

    /**
     * This method is used to update a promotion
     *
     * @param id the id of the promotion to be updated
     * @param promotionRequestDTO the promotion information to update
     * @return the updated promotion {@link PromotionDTO}
     * @throws EntityNotFoundException if the promotion already exists
     */
    @Override
    public PromotionDTO updatePromotion(String id, PromotionRequestDTO promotionRequestDTO) throws EntityNotFoundException {
        // check if the promotion exists
        Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        // check if the vendor exists
        vendorService.findVendorEntityById(promotionRequestDTO.getVendorId());

        // load the users from the request users ids
        List<User> users = userService.findUsersByIds(promotionRequestDTO.getUserIds());

        // convert list of users to set of users
        Set<User> usersSet = new HashSet<>(users);

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // check if the user is a merchant or a user, if the user is a merchant then check if the promotion related to the merchant
        // if the user is a user then check if this user is an admin
        if (userPrincipal.getUser() != null) {
            if (!userPrincipal.getUser().getRole().equals(UserRole.ROLE_ADMIN)) {
                throw new EntityNotFoundException("User not found");
            }
        } else {
            if (userPrincipal.getMerchant() == null) {
                throw new EntityNotFoundException("Merchant not found");
            }
            if (!userPrincipal.getMerchant().getId().equals(promotion.getMerchant().getId())) {
                throw new EntityNotFoundException("Merchant not found");
            }
        }

        // only admin can change the status
        if (userPrincipal.getUser() != null && promotionRequestDTO.getStatus() != null) {
            promotion.setStatus(promotionRequestDTO.getStatus());
        }

        // update the promotion entity
        promotion.setName(promotionRequestDTO.getName());
        promotion.setDescription(promotionRequestDTO.getDescription());
        promotion.setStartDate(TimeUtils.convertStringToLocalDateTime(promotionRequestDTO.getStartDate()));
        promotion.setEndDate(TimeUtils.convertStringToLocalDateTime(promotionRequestDTO.getEndDate()));
        if (promotionRequestDTO.getIsActive() != null) {
            promotion.setIsActive(promotionRequestDTO.getIsActive());
        }

        // load the users from promotion
        Set<User> promotionUsers = promotion.getUsers();

        // combine the users from promotion and request
        // let the users if they are already in the promotion and add the new users and remove the users that are not in
        // the request
        promotionUsers.addAll(usersSet);
        promotionUsers.retainAll(usersSet);

        // set the users to promotion
        promotion.setUsers(promotionUsers);

        // save the promotion
        promotion = promotionRepository.save(promotion);

        return promotion.toDTO();
    }

    /**
     * This method is used to delete a promotion
     *
     * @param id the id of the promotion to be deleted
     * @throws EntityNotFoundException if the promotion does not exist
     */
    @Override
    public void deletePromotion(String id) throws EntityNotFoundException {
        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // check if the one who try to delete is a merchant then check if the promotion related to the merchant
        // if the one who try to delete is a user then check if this user is an admin
        if (userPrincipal.getUser() != null) {
            if (!userPrincipal.getUser().getRole().equals(UserRole.ROLE_ADMIN)) {
                throw new EntityNotFoundException("User not found");
            }
        } else {
            if (userPrincipal.getMerchant() == null) {
                throw new EntityNotFoundException("Merchant not found");
            }
            Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
            if (!userPrincipal.getMerchant().getId().equals(promotion.getMerchant().getId())) {
                throw new EntityNotFoundException("Merchant not found");
            }
        }

        try {
            promotionRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityNotFoundException("Promotion not found");
        }
    }

    /**
     * This method is used to get a promotion by given id
     *
     * @param id the id of the promotion to be obtained
     * @return the promotion data {@link PromotionDTO}
     * @throws EntityNotFoundException if the promotion does not exist
     */
    @Override
    public PromotionDTO getPromotion(String id) throws EntityNotFoundException {
        return promotionRepository.findById(id)
                .map(Promotion::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
    }

    /**
     * This method is used to get all promotions
     *
     * @param name the name of the promotion to be obtained
     * @param isActive the status of the promotion to be obtained
     * @param startDateFrom the start date from of the promotion to be obtained
     * @param startDateTo the start date to of the promotion to be obtained
     * @param endDateFrom the end date from of the promotion to be obtained
     * @param endDateTo the end date to of the promotion to be obtained
     * @param page the pagination information
     * @return the list of promotions {@link PromotionDTO}
     */
    @Override
    public Page<PromotionDTO> getAllPromotions(String name, Boolean isActive, String startDateFrom, String startDateTo,
                                               String endDateFrom, String endDateTo, Pageable page) {
        // prepare the filter specification
        Specification<Promotion> specification = PromotionSpecifications.filterPromotions(
                name,
                isActive,
                startDateFrom,
                startDateTo,
                endDateFrom,
                endDateTo
        );
        return promotionRepository.findAll(specification, page)
                .map(Promotion::toDTO);
    }

    /**
     * This method is used to return the list of promotions for the given scan data
     *
     * @param scanDTO the scan data
     * @throws EntityNotFoundException if the promotion does not exist
     * @throws BadRequestException if the ScanDTO contains invalid message
     */
    @Override
    public List<PromotionDTO> promotionScannerValidator(ScanDTO scanDTO) throws EntityNotFoundException, BadRequestException {
        // get the authenticated validator
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getValidator() == null) {
            throw new EntityNotFoundException("Validator not found");
        }

        // get the validator entity
        Validator validator = validatorService.getValidatorEntityById(userPrincipal.getValidator().getId());

        // decrypt the message
        MaCryptoUtils maCryptoUtils = new MaCryptoUtils();
        String decryptedMessage = maCryptoUtils.decryptAES(scanDTO.getMessage(), PropertyLoader.getAesSecret());
        log.info("decryptedMessage: {}", decryptedMessage);

        if (decryptedMessage == null || decryptedMessage.isEmpty()) {
            throw new BadRequestException("Not an iConnect QR Code! Failed to Decrypt the data.");
        }

        // convert the decrypted message to ScannerMessageDTO Object using gson
        Gson gson = new Gson();
        ScannerMessageDTO scannerMessageDTO = gson.fromJson(decryptedMessage, ScannerMessageDTO.class);
        log.info("scannerMessageDTO: {}", scannerMessageDTO);

        // find the user by user id
        User user = userService.findUserEntityById(scannerMessageDTO.getUserId());

        // get the current date time as local date time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // find promotion by vendor and user and start date less than current date time and end date greater than current date time
        // and is active true and promotion status is approved
        return promotionRepository.findByVendorAndUsersAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsActiveTrueAndStatus
                        (validator.getVendor(), user, currentDateTime, currentDateTime, PromotionStatus.APPROVED).stream()
                .map(Promotion::toScannerResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * This method is used to get all vendor promotions
     *
     * @param vendorId the vendor id to get promotions for
     * @return the list of promotions {@link PromotionDTO}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public List<PromotionDTO> getVendorPromotions(String vendorId) throws EntityNotFoundException {
        // check if the vendor exists
        Vendor vendor = vendorService.findVendorEntityById(vendorId);
        log.info("Vendor: {}", vendor);
        return promotionRepository.findByVendor(vendor).stream().map(Promotion::toDTO).collect(Collectors.toList());
    }

    /**
     * This method is used encrypt the user id using AES
     *
     * @param scannerMessageDTO the user id information {@link ScannerMessageDTO}
     * @return the encrypted user id
     * @throws BadRequestException if the user id is not valid
     */
    @Override
    public HashMap<String, Object> encrypt(ScannerMessageDTO scannerMessageDTO) throws BadRequestException {
        // check if scanner message is null
        if (scannerMessageDTO == null || scannerMessageDTO.getUserId().isEmpty()) {
            throw new BadRequestException("Scanner message is null");
        }

        // convert scanner message to json
        Gson gson = new Gson();
        String json = gson.toJson(scannerMessageDTO);
        log.info("Scanner message json: {}", json);
        // encrypt the json
        MaCryptoUtils maCryptoUtils = new MaCryptoUtils();
        String encrypted = maCryptoUtils.encryptAES(json, PropertyLoader.getAesSecret());
        // add the encrypted message to the response
        HashMap<String, Object> response = new HashMap<>();
        response.put("encrypted", encrypted);

        return response;
    }
}
