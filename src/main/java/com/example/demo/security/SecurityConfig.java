package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API通信のために必須
                .formLogin(form -> form
                        .loginPage("/login")
                        // ★ここを追加！勝手なリダイレクトを防ぐ設定
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // 302ではなく200を返す
                        })
                        .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                        // ログイン関連は誰でもOK
                        .requestMatchers("/login", "/api/auth/login", "/css/**", "/js/**").permitAll()

                        // ★ここを hasRole("ADMIN") ではなく authenticated() に変える
                        // これで「誰でもいいからログインしてれば通す」状態になります
                        .requestMatchers("/work_submit").authenticated()

                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

