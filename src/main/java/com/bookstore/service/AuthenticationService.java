package com.bookstore.service;

import com.bookstore.dto.UserLoginRequestDto;
import com.bookstore.dto.UserLoginResponseDto;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserLoginResponseDto login(UserLoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserLoginResponseDto(token);
    }
}
