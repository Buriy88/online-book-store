package com.bookstore.service;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto registerUser(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("User with this email exist");
        }

        User user = userMapper.toModel(request);
        user.setPassword(request.getPassword());
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
