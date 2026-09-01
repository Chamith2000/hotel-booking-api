package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

