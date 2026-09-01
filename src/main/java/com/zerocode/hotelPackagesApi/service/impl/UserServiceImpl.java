package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.request.AuthRequest;
import com.zerocode.hotelPackagesApi.exception.UserAlreadyExistsException;
import com.zerocode.hotelPackagesApi.model.Authority;
import com.zerocode.hotelPackagesApi.model.User;
import com.zerocode.hotelPackagesApi.repository.UserRepository;
import com.zerocode.hotelPackagesApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(AuthRequest authRequest) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already registered");
        }

        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        newUser.setEnabled(true);

        Authority userAuthority = new Authority();
        userAuthority.setAuthority("ROLE_USER");
        userAuthority.setUser(newUser);
        userRepository.save(newUser);

        newUser.setAuthorities(Collections.singletonList(userAuthority));
        userRepository.save(newUser);
    }
}