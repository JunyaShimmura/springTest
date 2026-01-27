package com.example.demo.api;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private UserRepository userRepository; // もしくはService経由で
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login") // パスをAPI用に少しずらすのが安全
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(req.getUsername());

        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            // 2. パスワード比較
            if (passwordEncoder.matches(req.getPassword(),user.getPassword())) {
                // 3. 権限セット (ROLE_ADMIN)
                String roleWithPrefix = "ROLE_" + user.getRoles();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        AuthorityUtils.createAuthorityList(roleWithPrefix)
                );

                //  認証情報をセット
                SecurityContextHolder.getContext().setAuthentication(auth);

                // ：セッションに認証コンテキストを保存（403対策）
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                // Sessionにユーザーオブジェクトを保存
                session.setAttribute("user", user);

                return ResponseEntity.ok(Map.of("status", "success"));
            }
        }

        // ユーザーがいない、またはパスワード間違い
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", "error", "message", "ユーザー名かパスワードが違います"));
    }
}