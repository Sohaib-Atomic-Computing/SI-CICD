package io.satra.iconnect.service.merchant;

import io.satra.iconnect.dto.MerchantDTO;
import io.satra.iconnect.dto.request.LoginRequestDTO;
import io.satra.iconnect.dto.response.JwtResponseDTO;
import io.satra.iconnect.entity.Merchant;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface MerchantService {

    /**
     * Register a new merchant
     *
     * @param merchant Merchant to register
     * @param logo     Merchant logo
     * @return Registered merchant {@link JwtResponseDTO}
     * @throws BadRequestException if the merchant already exists
     * @throws IOException         if the logo is not uploaded successfully
     */
    JwtResponseDTO register(Merchant merchant, MultipartFile logo) throws BadRequestException, IOException;

    /**
     * This method is used to log in a merchant
     *
     * @param loginRequestDTO the merchant information to log in
     *                        email and password
     * @return the logged-in merchant with a JWT token {@link JwtResponseDTO}
     * @throws BadRequestException if the user does not exist or the password is incorrect
     */
    JwtResponseDTO loginMerchant(LoginRequestDTO loginRequestDTO) throws BadRequestException;

    /**
     *  This method is used to update the merchant profile.
     *
     *  @param merchant the merchant to be updated
     *                  {@link Merchant}
     * @return the updated merchant {@link MerchantDTO}
     * @throws EntityNotFoundException if the merchant does not exist
     * @throws BadRequestException if the merchant is not updated successfully
     */
    MerchantDTO updateMerchant(Merchant merchant) throws EntityNotFoundException, BadRequestException;

    /**
     * This method is used to update the merchant profile by merchant id.
     *
     * @param id       the id of the merchant to be updated
     * @param merchant the merchant information to be updated
     *                 {@link Merchant}
     * @return the updated merchant {@link MerchantDTO}
     * @throws EntityNotFoundException if the merchant is not found
     * @throws BadRequestException     if the merchant information is not valid
     */
    MerchantDTO updateMerchantById(String id, Merchant merchant) throws EntityNotFoundException, BadRequestException;

    /**
     * This method is used to update the merchant logo by merchant id.
     *
     * @param id       the id of the merchant to be updated
     * @param logo     the merchant logo to be updated
     * @return the updated merchant {@link MerchantDTO}
     * @throws EntityNotFoundException if the merchant is not found
     * @throws BadRequestException     if the merchant information is not valid
     * @throws IOException             if the logo is not uploaded successfully
     */
    MerchantDTO updateMerchantById(String id, MultipartFile logo) throws EntityNotFoundException, BadRequestException, IOException;

    /**
     * This method is used to get the merchant entity by given email
     *
     * @param email the email address of the merchant account to be obtained
     * @return merchant the merchant entity {@link Merchant}
     * @throws EntityNotFoundException if the merchant does not exist
     */
    Merchant getMerchantEntityByEmail(String email) throws EntityNotFoundException;
}
