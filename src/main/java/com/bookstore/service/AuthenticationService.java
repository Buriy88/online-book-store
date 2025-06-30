package com.bookstore.service;

import com.bookstore.dto.UserLoginRequestDto;
import com.bookstore.dto.UserLoginResponseDto;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserLoginResponseDto login(UserLoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User with email " + request.getEmail() + " not found"));

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserLoginResponseDto(token);
    }
}
