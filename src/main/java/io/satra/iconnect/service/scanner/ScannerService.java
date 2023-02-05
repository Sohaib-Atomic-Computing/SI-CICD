package io.satra.iconnect.service.scanner;

import io.satra.iconnect.dto.scandto.ScanDTO;
import io.satra.iconnect.dto.scandto.ScannerUserInfoDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface ScannerService {

    /**
     * This method is used to get the scanner QR code and decrypt it and return the
     * information inside the QR Code.
     *
     * @param scanDTO the scan information
     * @throws EntityNotFoundException if the user does not exist or not active
     * @throws BadRequestException if the QR code is not an IConnect QR Code
     * @return {@link ScannerUserInfoDTO} the user information with the timestampUTC
     */
    ScannerUserInfoDTO scan(ScanDTO scanDTO) throws EntityNotFoundException, BadRequestException;

}
