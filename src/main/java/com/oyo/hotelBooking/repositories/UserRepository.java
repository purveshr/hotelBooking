package com.oyo.hotelBooking.repositories;

import com.oyo.hotelBooking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmailId(String emailId);
    boolean existsByNumber(String number);

    @Query("SELECT u.role FROM User u WHERE u.emailId = :emailId")
    String getRoleByEmailId(@Param("emailId") String emailId);

}
