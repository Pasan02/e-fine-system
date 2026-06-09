package com.slpolice.trafficfines.auth.service.impl;

import com.slpolice.trafficfines.auth.dto.LoginRequest;
import com.slpolice.trafficfines.auth.dto.LoginResponse;
import com.slpolice.trafficfines.auth.dto.RegisterRequest;
import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.auth.service.AuthService;
import com.slpolice.trafficfines.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return LoginResponse.builder()
                .accessToken(jwt)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(registerRequest.getRole())
                .district(registerRequest.getDistrict())
                .build();

        return userRepository.save(user);
    }
}
