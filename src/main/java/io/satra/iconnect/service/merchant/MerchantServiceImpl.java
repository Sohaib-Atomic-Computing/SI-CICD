package io.satra.iconnect.service.merchant;

import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.entity.enums.UserRole;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.MerchantRepository;
import io.satra.iconnect.security.UserPrincipal;
import io.satra.iconnect.utils.FileUtils;
import io.satra.iconnect.utils.JWTToken;
import io.satra.iconnect.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTToken jwtToken;

    @Override
    public JwtResponseDTO register(Merchant merchant, MultipartFile logo) throws BadRequestException, IOException{
        log.info("Registering merchant: {}", merchant);
        // check if the merchant already exists
        if (merchantRepository.findFirstByAdminEmailOrMobile(merchant.getAdminEmail(), merchant.getMobile()).isPresent()) {
            throw new BadRequestException("Merchant email or mobile number already exists!");
        }

        // validate the merchant email and mobile number
        ValidateUtil vd = new ValidateUtil();

        if (!vd.checkEmailValidation(merchant.getAdminEmail())) {
            throw new BadRequestException("Invalid email address! Please provide proper email address");
        }
        log.info("Mobile number: {}", merchant.getMobile());
        if (!vd.checkMobileNumberValidation(merchant.getMobile())) {
            if(merchant.getMobile() != null && !merchant.getMobile().equals("")
                    && !(merchant.getMobile().trim().startsWith("00") || merchant.getMobile().trim().startsWith("+"))) {
                throw new BadRequestException("Invalid mobile number! The mobile number should start with 00 or +");
            }
            throw new BadRequestException("Invalid mobile number! Please provide proper mobile number");
        }

        // save the merchant logo
        String folderName = merchant.getName().toLowerCase().replaceAll(" ", "_");
        String logoUrl = new FileUtils().saveFile(Collections.singletonList(logo), folderName);
        if (logoUrl == null) {
            throw new BadRequestException("Logo is not uploaded successfully!");
        }
        merchant.setLogo(logoUrl);
        String password = merchant.getPassword();
        merchant.setPassword(passwordEncoder.encode(password));
        merchantRepository.save(merchant);

        // generate JWT token
        String token = jwtToken.generate(merchant.getAdminEmail(), password);
        return JwtResponseDTO.builder()
                .merchant(merchant.toDTO())
                .token(token)
                .build();
    }

    @Override
    public JwtResponseDTO loginMerchant(LoginRequestDTO loginRequestDTO) throws BadRequestException {
        Merchant merchant = merchantRepository.findFirstByAdminEmail(loginRequestDTO.getEmailOrMobile())
                .orElseThrow(() -> new BadRequestException("Merchant does not exist!"));

        // generate JWT token
        String token = jwtToken.generate(loginRequestDTO.getEmailOrMobile(), loginRequestDTO.getPassword());
        return JwtResponseDTO.builder()
                .merchant(merchant.toDTO())
                .token(token)
                .build();
    }

    @Override
    public MerchantDTO updateMerchant(Merchant merchant) throws EntityNotFoundException, BadRequestException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal.getMerchant() == null) {
            log.error("Merchant not found!");
            throw new EntityNotFoundException("Merchant not found");
        }

        Merchant merchantEntity = merchantRepository.findFirstByAdminEmail(userPrincipal.getMerchant().getAdminEmail())
                .orElseThrow(() -> new EntityNotFoundException("Merchant does not exist!"));

        // email, mobile, and isActive are not allowed to update by the merchant itself
        if (merchant.getAdminEmail() != null && !merchant.getAdminEmail().equals(merchantEntity.getAdminEmail())) {
            throw new BadRequestException("Email is not allowed to update!");
        }
        if (merchant.getMobile() != null && !merchant.getMobile().equals(merchantEntity.getMobile())) {
            throw new BadRequestException("Mobile number is not allowed to update!");
        }
        merchant.setIsActive(null);

        return updateMerchantEntityByMerchant(merchantEntity, merchant);
    }

    @Override
    public MerchantDTO updateMerchantById(String id, Merchant merchant) throws EntityNotFoundException, BadRequestException {
        // get the merchant by id
        Merchant merchantEntity = merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant does not exist!"));

        return updateMerchantEntityByMerchant(merchantEntity, merchant);
    }

    @Override
    public MerchantDTO updateMerchantById(String id, MultipartFile logo)
            throws EntityNotFoundException, BadRequestException, IOException {
        // get the merchant by id
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant does not exist!"));

        // get the merchant logo
        String currentMerchantLogo = merchant.getLogo();
        // save the merchant logo
        String folderName = merchant.getName().toLowerCase().replaceAll(" ", "_");
        String logoUrl = new FileUtils().saveFile(Collections.singletonList(logo), folderName);
        if (logoUrl == null) {
            throw new BadRequestException("Logo is not uploaded successfully!");
        }
        merchant.setLogo(logoUrl);

        // delete the current merchant logo
        if (currentMerchantLogo != null) {
            new FileUtils().deleteFile(currentMerchantLogo);
        }

        merchantRepository.save(merchant);
        return merchant.toDTO();
    }

    @Override
    public Merchant getMerchantEntityByEmail(String email) throws EntityNotFoundException {
        return merchantRepository.findFirstByAdminEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Merchant does not exist!"));
    }

    /**
     * This method is used to update merchant by the given merchant entity and the merchant information to be updated
     *
     * @param merchantEntity the merchant entity to be updated {@link Merchant}
     * @param merchant       the merchant information that used to update the merchant entity {@link Merchant}
     * @return the merchant {@link MerchantDTO}
     * @throws EntityNotFoundException if the merchant does not exist
     */
    private MerchantDTO updateMerchantEntityByMerchant(Merchant merchantEntity, Merchant merchant) throws BadRequestException {
        // map the merchant information to the merchant entity
        if (merchant.getName() != null) {
            merchantEntity.setName(merchant.getName());
        }
        if (merchant.getAbbreviation() != null) {
            merchantEntity.setAbbreviation(merchant.getAbbreviation());
        }
        if (merchant.getMobile() != null) {
            merchantEntity.setMobile(merchant.getMobile());
        }
        if (merchant.getAdminEmail() != null) {
            merchantEntity.setAdminEmail(merchant.getAdminEmail());
        }
        if (merchant.getIsActive() != null) {
            merchantEntity.setIsActive(merchant.getIsActive());
        }
        if (merchant.getAdminFirstName() != null) {
            merchantEntity.setAdminFirstName(merchant.getAdminFirstName());
        }
        if (merchant.getAdminLastName() != null) {
            merchantEntity.setAdminLastName(merchant.getAdminLastName());
        }
        if (merchant.getFirstAddress() != null) {
            merchantEntity.setFirstAddress(merchant.getFirstAddress());
        }
        if (merchant.getSecondAddress() != null) {
            merchantEntity.setSecondAddress(merchant.getSecondAddress());
        }
        if (merchant.getCity() != null) {
            merchantEntity.setCity(merchant.getCity());
        }
        if (merchant.getState() != null) {
            merchantEntity.setState(merchant.getState());
        }
        if (merchant.getCountry() != null) {
            merchantEntity.setCountry(merchant.getCountry());
        }

        // save the merchant entity
        merchantRepository.save(merchantEntity);
        return merchantEntity.toDTO();
    }
}
