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
    public UserEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder, AuthProvider provider) {
        final UserEntity originalUser = userRepository.findByEmail(email);

        // getByCredentials : 진행상황보고 코드 //
        System.out.println("google User getByCredentials: " + originalUser.getEmail());
        ////////////////////////////////////

        if (originalUser != null && provider == AuthProvider.LOCAL && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        else if (originalUser != null && provider == AuthProvider.GOOGLE) {
            System.out.println("구글 유저 반환");
            return originalUser;
        }
        return null;
    }

    public UserEntity saveOrUpdateGoogleUser(String googleEmail, String name) {
        UserEntity user = userRepository.findByEmail(googleEmail);
        if (user == null) {
            System.out.println("no user");
            user = UserEntity.builder()
                    .email(googleEmail)
                    .username(name)
                    .password("") // 구글 로그인 사용자는 비밀번호가 없으므로 빈 문자열
                    .provider(AuthProvider.GOOGLE)
                    .build();

            System.out.println("Saving new Google user!");
            return userRepository.save(user);

        } else if (user.getUsername() != name){ // 구글 닉네임이 바꼈을 경우 사용자 이름 업데이트
            System.out.println("Updating user name.");
            user.setUsername(name);
            user.setProvider(AuthProvider.GOOGLE);

            System.out.println("Saving updated user.");
            userRepository.save(user);

            return user;

        } else {
            return null;
        }
    }
}
