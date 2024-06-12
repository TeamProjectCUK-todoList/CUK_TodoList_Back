package com.example.todo.controller;

import com.example.todo.dto.UserDTO;
import com.example.todo.googleDTO.GoogleInfResponse;
import com.example.todo.googleDTO.GoogleRequest;
import com.example.todo.googleDTO.GoogleResponse;
import com.example.todo.model.UserEntity;
import com.example.todo.properties.GoogleClientProperties;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleUserController {

    private final GoogleClientProperties googleClientProperties;
    private final UserService userService; //

    @Autowired
    public GoogleUserController(GoogleClientProperties googleClientProperties, UserService userService) {
        this.googleClientProperties = googleClientProperties;
        this.userService = userService; //
    }

    @RequestMapping(value = "/api/v1/oauth2/google", method = RequestMethod.POST)
    public String loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                + googleClientProperties.getId()
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google/callback&response_type=code&scope=email%20profile%20openid&access_type=offline";

        return reqUrl;
    }

    @RequestMapping(value="/api/v1/oauth2/google/callback", method = RequestMethod.GET)
    public ResponseEntity<?> loginGoogle(@RequestParam(value = "code") String authCode){
        RestTemplate restTemplate = new RestTemplate();
        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(googleClientProperties.getId())
                .clientSecret(googleClientProperties.getPw())
                .code(authCode)
                .redirectUri("http://localhost:8080/api/v1/oauth2/google/callback")
                .grantType("authorization_code").build();
        ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);

        String jwtToken=resultEntity.getBody().getId_token();

        Map<String, String> map=new HashMap<>();
        map.put("id_token",jwtToken);
        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfResponse.class);

        String email = resultEntity2.getBody().getEmail();
        String name = resultEntity2.getBody().getName();

        // 사용자 정보 저장
        UserEntity user = userService.saveOrUpdateGoogleUser(email, name);

        // 응답 객체 생성
        final UserDTO responseUserDTO = UserDTO.builder()
                .email(user.getEmail())
                .id(user.getId())
                .token(jwtToken) // 구글에서 받은 JWT 토큰을 그대로 사용
                .build();

        return ResponseEntity.ok().body(responseUserDTO);
    }
}
