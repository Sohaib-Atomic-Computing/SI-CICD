package io.satra.iconnect.service.scanner;

import com.google.gson.Gson;
import io.satra.iconnect.dto.scandto.ScannerMessageDTO;
import io.satra.iconnect.dto.UserDTO;
import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.dto.scandto.ScannerUserInfoDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.service.user.UserService;
import io.satra.iconnect.utils.MaCryptoUtils;
import io.satra.iconnect.utils.PropertyLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScannerServiceImpl  implements ScannerService {

    @Autowired
    private UserService userService;

    /**
     * This method is used to get the scanner QR code and decrypt it and return the
     * information inside the QR Code.
     *
     * @param scanDTO the scan information
     * @throws EntityNotFoundException if the user does not exist or not active
     * @throws BadRequestException if the QR code is not an IConnect QR Code
     * @return {@link ScannerUserInfoDTO} the user information with the timestampUTC
     */
    @Override
    public ScannerUserInfoDTO scan(ScanDTO scanDTO) throws EntityNotFoundException, BadRequestException {
        // decrypt the message
        MaCryptoUtils maCryptoUtils = new MaCryptoUtils();
        String decryptedMessage = maCryptoUtils.decryptAES(scanDTO.getMessage(), PropertyLoader.getAesSecret());
        log.debug("Decrypted message: {}", decryptedMessage);

        // parse the decrypted message
        // convert the decrypted message to ScannerMessageDTO Object using gson
        Gson gson = new Gson();
        ScannerMessageDTO scannerMessageDTO = gson.fromJson(decryptedMessage, ScannerMessageDTO.class);
        log.debug("ScannerMessageDTO: {}", scannerMessageDTO);

        if (scannerMessageDTO == null) {
            throw new BadRequestException("Not an IConnect QR Code! Failed to Decrypt the data.");
        }

        // find the user by the user id
        UserDTO user = userService.findActiveUserEntityById(scannerMessageDTO.getUserId()).toViewDTO();

        return ScannerUserInfoDTO.builder()
                .user(user)
                .timestampUTC(scannerMessageDTO.getTimestampUTC())
                .build();
    }
}
