package com.example.demo.model;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "ユーザー名を入力してください")
    private String username;
    @NotBlank(message = "passwordを入力してください")
    private String password;
}