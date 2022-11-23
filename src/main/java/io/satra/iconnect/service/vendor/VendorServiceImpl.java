package io.satra.iconnect.service.vendor;

import io.satra.iconnect.dto.VendorDTO;
import io.satra.iconnect.dto.request.VendorRequestDTO;
import io.satra.iconnect.entity.Vendor;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.repository.VendorRepository;
import io.satra.iconnect.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    /**
     * This method is used to add a new vendor
     *
     * @param vendorRequestDTO the vendor information to register
     * @return the registered vendor {@link VendorDTO}
     * @throws BadRequestException if the vendor already exists
     */
    @Override
    public VendorDTO createVendor(VendorRequestDTO vendorRequestDTO) throws BadRequestException {
        // check if the vendor request data is valid
        if (vendorRequestDTO.getName() == null || vendorRequestDTO.getName().isEmpty()) {
            throw new BadRequestException("Vendor name is required");
        }

        // check if the vendor already exists
        if (vendorRepository.findByName(vendorRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Vendor already exists");
        }

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // create the vendor entity
        Vendor vendor = Vendor.builder()
                .name(vendorRequestDTO.getName())
                .createdBy(userPrincipal.getUser())
                .lastModifiedBy(userPrincipal.getUser())
                .build();

        // save the vendor
        vendor = vendorRepository.save(vendor);

        return vendor.toDTO();
    }

    /**
     * This method is used to update a vendor
     *
     * @param id the id of the vendor to be updated
     * @param vendorRequestDTO the vendor information to update
     * @return the updated vendor {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor already exists
     */
    @Override
    public VendorDTO updateVendor(String id, VendorRequestDTO vendorRequestDTO) throws EntityNotFoundException {
        // check if the vendor not exists
        Vendor vendor = vendorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No vendor with given id %s found!".formatted(id)));

        // get the user data from the request
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // update the vendor entity
        if (vendorRequestDTO.getName() != null && !vendorRequestDTO.getName().isEmpty()) {
            vendor.setName(vendorRequestDTO.getName());
        }

        // update the vendor last modified by
        vendor.setLastModifiedBy(userPrincipal.getUser());

        // save the vendor
        vendor = vendorRepository.save(vendor);

        return vendor.toDTO();
    }

    /**
     * This method is used to delete a vendor
     *
     * @param id the id of the vendor to be deleted
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public void deleteVendor(String id) throws EntityNotFoundException {
        try {
            vendorRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityNotFoundException("No vendor with given id %s found!".formatted(id));
        }
    }

    /**
     * This method is used to get a vendor by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor data {@link VendorDTO}
     * @throws EntityNotFoundException if the vendor does not exist
     */
    @Override
    public VendorDTO findVendorById(String id) throws EntityNotFoundException {
        Vendor vendor = vendorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No vendor with given id %s found!".formatted(id)));
        return vendor.toDTO();
    }

    /**
     * This method is used to get vendor entity by given id
     *
     * @param id the id of the vendor to be obtained
     * @return the vendor entity {@link Vendor}
     * @throws EntityNotFoundException
     */
    @Override
    public Vendor findVendorEntityById(String id) throws EntityNotFoundException {
        return vendorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No vendor with given id %s found!".formatted(id)));
    }

    /**
     * This method is used to get all vendors
     *
     * @param page the pagination information
     * @return the list of vendors {@link VendorDTO}
     */
    @Override
    public Page<VendorDTO> findAllVendors(Pageable page) {
        return vendorRepository.findAll(page).map(Vendor::toDTO);
    }
}
