package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.ChangeRoleDTO;
import com.oyo.hotelBooking.dtos.UserRequestDTO;
import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    public String registerUser(UserRequestDTO userRequestDto){
        if(userRepository.existsByEmailId(userRequestDto.getEmailId())){
            throw new RuntimeException("User with mail id already exists");
        }

        if(userRepository.existsByNumber(userRequestDto.getNumber())){
            throw new RuntimeException("User with Number already exists");
        }

        User user = new User();

        user.setNumber(userRequestDto.getNumber());
        user.setEmailId(userRequestDto.getEmailId());
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setRole(Roles.CUSTOMER);

        // Encoding Password
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "User registered successfully";
    }

    public Roles getUserRole(String emailId){
        if(!userRepository.existsByEmailId(emailId)){
            throw new RuntimeException("User with mail id does not exists");
        }
        return userRepository.getRoleByEmailId(emailId);
    }

    public Roles changeRole(ChangeRoleDTO dto){
        User user = userRepository.findByEmailId(dto.getEmailId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("User with email does not exist"));
        // Prevent redundant update
        if (user.getRole() == dto.getRole()) {
            throw new IllegalArgumentException("User already has this role");
        }

        user.setRole(dto.getRole());
        userRepository.save(user);
        return user.getRole(); // or return full User / DTO


    }


}
