package com.testpaper.demo.resources;

import com.testpaper.demo.dto.UserRequest;
import com.testpaper.demo.dto.UserResponse;
import com.testpaper.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        try {
            UserResponse response = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("cannot be empty")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponse(null, e.getMessage(), null, null));
            } else if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new UserResponse(null, e.getMessage(), null, null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserResponse(null, e.getMessage(), null, null));
            }
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String username) {
        try {
            UserResponse response = userService.getUserById(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "count", defaultValue = "20") Integer count) {
        try {
            List<UserResponse> users = userService.getAllUsers(start, count);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a specific error DTO
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username, @RequestBody UserRequest request) {
        try {
            UserResponse response = userService.updateUser(username, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Or a specific error DTO
            } else if (e.getMessage().contains("Username cannot be changed")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Or a specific error DTO
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a specific error DTO
            }
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
