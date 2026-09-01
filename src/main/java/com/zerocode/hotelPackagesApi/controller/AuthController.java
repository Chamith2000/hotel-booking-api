package com.zerocode.hotelPackagesApi.controller;
import com.zerocode.hotelPackagesApi.controller.request.AuthRequest;
import com.zerocode.hotelPackagesApi.exception.UserAlreadyExistsException;
import com.zerocode.hotelPackagesApi.security.JwtUtil;
import com.zerocode.hotelPackagesApi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping(value = "/register")
    public void register(@RequestBody AuthRequest authRequest) throws UserAlreadyExistsException {
        userService.register(authRequest);
    }

    @Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        log.info(authentication.toString());

        UserDetails userDetails = new User(request.getUsername(), "", authentication.getAuthorities());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
