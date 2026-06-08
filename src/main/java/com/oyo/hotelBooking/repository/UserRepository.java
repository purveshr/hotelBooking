package com.oyo.hotelBooking.repository;

import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmailId(String emailId);
    boolean existsByNumber(String number);
    Optional<User> findByEmailId(String emailId);

    @Query("SELECT u.role FROM User u WHERE u.emailId = :emailId")
    Roles getRoleByEmailId(@Param("emailId") String emailId);

}
