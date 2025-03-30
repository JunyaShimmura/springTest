package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import com.example.demo.security.CustomUserDetailsService;


import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    //private final  CustomUserDetailsService customUserDetailsService;
    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler ) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCryptを使ったPasswordEncoderを返す
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF保護を無効化 403エラー回避
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll()  // すべてのリクエストを許可  一時的
                        // その他のリクエストに対しては認証が必要
                        //.anyRequest().authenticated()  // それ以外のリクエストには認証を求める
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)  // ログイン成功時の処理
                        .failureUrl("/login?error=true")  // 認証失敗時のリダイレクト先
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // ログアウトURL
                        .logoutSuccessUrl("/login?logout")  // ログアウト後の遷移先
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 必要に応じてセッションを作成
                        .invalidSessionUrl("/login?invalid-session")  // セッションが無効の場合の遷移先
                        .maximumSessions(1)  // 同時セッション数を1に制限
                        .expiredUrl("/login?expired")  // セッション期限切れ時の遷移先
                );

        return http.build();
    }

    // AuthenticationManager の設定
//    @Bean
//    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//        // AuthenticationManagerBuilder を使って、ユーザー詳細情報サービスとパスワードエンコーダーを設定
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder
//                .userDetailsService(customUserDetailsService)  // CustomUserDetailsService を指定
//                .passwordEncoder(passwordEncoder());  // PasswordEncoder を指定
//
//        return authenticationManagerBuilder.build();
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        return new InMemoryUserDetailsManager(
//                User.withUsername("user1")
//                        .password(passwordEncoder().encode("pass12word"))
//                        .roles("USER")
//                        .build(),
//                User.withUsername("user2")
//                        .password(passwordEncoder().encode("pass12word"))
//                        .roles("USER")
//                        .build(),
//                User.withUsername("user3")
//                        .password(passwordEncoder().encode("pass12word"))
//                        .roles("USER")
//                        .build(),
//                User.withUsername("shinmura")
//                        .password(passwordEncoder().encode("pass12word"))
//                        .roles("ADMIN")
//                        .build()
//        );
//    }
}
