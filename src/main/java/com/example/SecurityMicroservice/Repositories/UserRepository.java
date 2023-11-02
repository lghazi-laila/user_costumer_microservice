package com.example.SecurityMicroservice.Repositories;

import com.example.SecurityMicroservice.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.firstName = :firstName")
    List<User> findByFirstName(@Param("firstName") String firstName);



}

