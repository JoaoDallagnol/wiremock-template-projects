package com.junit.wiremock.service;

import com.junit.wiremock.client.EmailValidationClient;
import com.junit.wiremock.dto.EmailValidationResponse;
import com.junit.wiremock.entity.User;
import com.junit.wiremock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final EmailValidationClient emailValidationClient;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        EmailValidationResponse validation = emailValidationClient.validateEmail(user.getEmail());
        if (!validation.isValid()) {
            throw new RuntimeException("Invalid email: " + validation.getReason());
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}