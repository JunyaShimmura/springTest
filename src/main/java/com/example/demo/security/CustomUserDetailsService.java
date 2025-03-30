package com.example.demo.security;

import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ユーザー情報をOptionalで取得
        Optional<Optional<UserEntity>> userEntityOptional = Optional.ofNullable(userRepository.findByUsername(username));
        // ユーザーが見つからない場合は例外をスロー
        Optional<UserEntity> userEntity = userEntityOptional.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));
        // UserDetails を作成して返す
        return User.builder()
                .username(userEntity.get().getUsername())
                .password(userEntity.get().getPassword())
                .roles("USER") // ロールなしでもデフォルトのUSERロールを設定（必須）
                .build();
                //.roles(userEntity.getRoles().stream().map(role -> role.getName()).toArray(String[]::new))
    }
}
