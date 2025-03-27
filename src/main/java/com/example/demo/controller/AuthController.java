package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   @Autowired
   private final UserRepository userRepository = null;

   @Autowired
   private PasswordEncoder passwordEncoder;

//   @PostMapping("/register")
//   public ResponseEntity<String> register(@RequestBody UserRecord userRecord) {
//       userRecord.setPassword(passwordEncoder.encode(userRecord.getPassword()));
//       userRecord.setRole("USER");
//       userRepository.save(userRecord);
//       return ResponseEntity.ok("ユーザー登録完了");
//   }
}
