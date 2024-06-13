package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.model.UserEntity;
import com.example.todo.persistence.UserRepository;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class IntegratedUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/myProvider")
    public ResponseEntity<?> getUserProvider(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("accessId");
            UserEntity user = userService.getUserById(userId);
            if (user != null) {
                UserDTO responseUserDTO = UserDTO.builder()
                        .email(user.getEmail())
                        .id(user.getId())
                        .username(user.getUsername())
                        .provider(user.getProvider())
                        .build();
                return ResponseEntity.ok().body(responseUserDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
