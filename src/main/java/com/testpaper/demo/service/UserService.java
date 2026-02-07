package com.testpaper.demo.service;

import com.testpaper.demo.dto.UserRequest;
import com.testpaper.demo.dto.UserResponse;
import com.testpaper.demo.model.User;
import com.testpaper.demo.repository.UserRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }

        if (userRepository.findById(request.getUsername()).isPresent()) {
            throw new RuntimeException(String.format("User with username '%s' already exists", request.getUsername()));
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException(String.format("User with email '%s' already exists", request.getEmail()));
        }

        User user = new User(request.getUsername(), request.getName(), request.getEmail(), request.getDateOfBirth());
        if (request.getAccesstype() != null && !request.getAccesstype().trim().isEmpty()) {
            user.setAccesstype(request.getAccesstype());
        } else {
            user.setAccesstype("Student"); // Default value
        }
        user = userRepository.save(user);
        return new UserResponse(user.getName(), user.getUsername(), user.getEmail(), user.getDateOfBirth(), user.getAccesstype());
    }

    public UserResponse getUserById(String username) {
        Optional<User> userOptional = userRepository.findById(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        return new UserResponse(user.getName(), user.getUsername(), user.getEmail(), user.getDateOfBirth(), user.getAccesstype());
    }

    public List<UserResponse> getAllUsers(Integer start, Integer count) {
        List<User> users;
        if (start == null || count == null || (start == 0 && count == 0)) {
            Pageable pageable = PageRequest.of(0, 20);
            users = userRepository.findAll(pageable).getContent();
        } else {
            Pageable pageable = PageRequest.of(start, count);
            users = userRepository.findAll(pageable).getContent();
        }
        return users.stream()
                .map(user -> new UserResponse(user.getName(), user.getUsername(), user.getEmail(), user.getDateOfBirth(), user.getAccesstype()))
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(String username, UserRequest request) {
        Optional<User> userOptional = userRepository.findById(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        // Username is the ID, so it cannot be changed via update
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty() && !request.getUsername().equals(user.getUsername())) {
            throw new RuntimeException("Username cannot be changed as it is the primary key");
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException(String.format("User with email '%s' already exists", request.getEmail()));
            }
            user.setEmail(request.getEmail());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getAccesstype() != null && !request.getAccesstype().trim().isEmpty() && !request.getAccesstype().equals(user.getAccesstype())) {
            user.setAccesstype(request.getAccesstype());
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return new UserResponse(user.getName(), user.getUsername(), user.getEmail(), user.getDateOfBirth(), user.getAccesstype());
    }

    public void deleteUser(String username) {
        if (!userRepository.existsById(username)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(username);
    }
}
