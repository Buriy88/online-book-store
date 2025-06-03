package com.bookstore.controller;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        System.out.println(">>>> Спроба реєстрації: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("User with email already exists");
        }

        User user = userMapper.toModel(request);
        user.setPassword(request.getPassword());

        System.out.println(">>>> Користувач: " + user);

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

}
