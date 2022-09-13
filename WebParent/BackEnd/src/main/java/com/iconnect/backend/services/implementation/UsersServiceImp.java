package com.iconnect.backend.services.implementation;

import com.iconnect.backend.Utils.Utils;
import com.iconnect.backend.dtos.QRCodeDTO;
import com.iconnect.backend.dtos.RegisterRequest;
import com.iconnect.backend.dtos.UpdateProfileRequest;
import com.iconnect.backend.exception.BadRequestException;
import com.iconnect.backend.exception.ForbiddenRequestException;
import com.iconnect.backend.exception.RecordNotFoundException;
import com.iconnect.backend.model.Users;
import com.iconnect.backend.repository.UsersRepository;
import com.iconnect.backend.services.UsersService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import com.google.gson.Gson;

@Service
public class UsersServiceImp implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public Page<Users> findAll(Pageable pgbl) {
        return usersRepository.findAll(pgbl);
    }

    @Override
    public Users findByEmail(String email) {
        return null;
    }

    @Override
    public Users findById(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("No User record exist for given id", id));
        return user;
    }

    @Override
    public Users save(RegisterRequest registerRequest) throws RecordNotFoundException {

        Optional<Users> user = usersRepository.findByEmailAndIsActive(registerRequest.getEmail().toLowerCase(),Boolean.TRUE);

        if (user.isPresent()) {
            throw new BadRequestException("Email Already Exist" );
        }

        user = usersRepository.findByPhoneNumberAndIsActive(registerRequest.getPhoneNumber().toLowerCase(),Boolean.TRUE);

        if (user.isPresent()) {
            throw new BadRequestException("Phone Number Already Exist");
        }

        Users newUser = new Users();

        user = usersRepository.findByPhoneNumberAndIsActive(registerRequest.getPhoneNumber().toLowerCase(),Boolean.FALSE);

        if (user.isPresent()) {
            newUser = user.get();
        }
        else
        {
            user = usersRepository.findByEmailAndIsActive(registerRequest.getPhoneNumber().toLowerCase(),Boolean.FALSE);
            if (user.isPresent()) {
                newUser = user.get();
            }
        }
        newUser.setEmail(registerRequest.getEmail().toLowerCase());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber().toLowerCase());
        newUser.setFullName(registerRequest.getFullname().toLowerCase());
        newUser.setUserUniqueId(UUID.randomUUID().toString().replace("-","").substring(0,8));
        newUser.setDpUrl(registerRequest.getDpUrl());
        newUser.setResetToken(UUID.randomUUID().toString());
        newUser.setPassword(encoder.encode(registerRequest.getPassword()));
        newUser.setOTPCode(encoder.encode("00000"));
        newUser.setQRCode(createQRCode(newUser));
        Users savedUser = usersRepository.save(newUser);
        return savedUser;
    }

    @Override
    public Users update(Long userId, UpdateProfileRequest updateProfileRequest) {
        Users updatedUser = findById(userId);
        if (updateProfileRequest.getFullname() != null) {
            updatedUser.setFullName(updateProfileRequest.getFullname().toLowerCase());
        }
        if (updateProfileRequest.getDpUrl() != null) {
            updatedUser.setDpUrl(updateProfileRequest.getDpUrl().toLowerCase());
        }
        if (updateProfileRequest.getPassword() != null) {
            updatedUser.setPassword(encoder.encode(updateProfileRequest.getPassword()));
        }
        return usersRepository.save(updatedUser);
    }

    @Override
    public void delete(Long id) throws RecordNotFoundException {
        usersRepository.deleteById(id);
    }

    @Override
    public Page<Users> searchUsers(String phonenumber, Pageable pgbl) {
        return usersRepository.findByPhoneNumberIgnoreCaseContains(phonenumber, pgbl);
    }

    @Override
    public Boolean generateOTP(String phoneNumber) {

        Users user  = usersRepository.findByPhoneNumber(phoneNumber.toLowerCase()).orElseThrow(()
                -> new BadRequestException("Phone Number Not Found   : " + phoneNumber));
            user.setOTPCode(encoder.encode("00000"));
            usersRepository.save(user);
            return true;

    }


    private String createQRCode (Users user)
    {
        Gson gson = new Gson();

        QRCodeDTO QRCodeDto = new QRCodeDTO();
        QRCodeDto.setUniqueID(user.getUserUniqueId());
        QRCodeDto.setTimestamp(Utils.getCurrentTimeStamp());
        QRCodeDto.setRandomID(UUID.randomUUID().toString().replace("-","").substring(0,8));
        String output = gson.toJson(QRCodeDto);
        try {
            return Utils.encodeBase64(output) ;
        } catch (NoSuchAlgorithmException e) {
           return null;
        }
    }
}
