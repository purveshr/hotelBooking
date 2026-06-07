package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.UserRequestDto;
import com.oyo.hotelBooking.models.User;
import com.oyo.hotelBooking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    public String registerUser(UserRequestDto userRequestDto){
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
        user.setRole("USER");

        // Encoding Password
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "User registered successfully";
    }

    public String getUserRole(String emailId){
        if(!userRepository.existsByEmailId(emailId)){
            throw new RuntimeException("User with mail id does not exists");
        }

        return userRepository.getRoleByEmailId(emailId);
    }
}
