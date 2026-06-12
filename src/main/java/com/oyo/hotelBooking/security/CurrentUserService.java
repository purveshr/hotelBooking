package com.oyo.hotelBooking.security;

import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException;
import com.oyo.hotelBooking.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailId(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}

