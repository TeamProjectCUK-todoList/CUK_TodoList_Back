package com.example.todo.service;

import com.example.todo.model.UserEntity;
import com.example.todo.persistence.UserRepository;
import com.example.todo.provider.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 회원가입 코드
    public UserEntity create(final UserEntity userEntity) {
        if (userEntity == null || userEntity.getEmail() == null) {
            throw new RuntimeException("Invalid arguments");
        }

        final String email = userEntity.getEmail();

        if(userRepository.existsByEmail(email)){
            log.warn("Email already exists {}", email);
            throw new RuntimeException("Email already exists");
        }

        // 로컬 유저라는 것을 표시
        userEntity.setProvider(AuthProvider.LOCAL);

        return userRepository.save(userEntity);
    }

    // 로그인 시 확인 코드
    public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        final UserEntity originalUser = userRepository.findByEmail(email);

        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    public UserEntity saveOrUpdateGoogleUser(String email, String name) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            user = UserEntity.builder()
                    .email(email)
                    .username(name)
                    .password("") // 구글 로그인 사용자는 비밀번호가 없으므로 빈 문자열
                    .provider(AuthProvider.GOOGLE)
                    .build();
        } else { // 구글 닉네임이 바꼈을 경우 사용자 이름 업데이트
            user.setUsername(name);
            user.setProvider(AuthProvider.GOOGLE);
        }
        return userRepository.save(user);
    }
}
