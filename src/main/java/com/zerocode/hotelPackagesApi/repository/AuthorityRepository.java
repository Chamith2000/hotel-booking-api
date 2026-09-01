package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}