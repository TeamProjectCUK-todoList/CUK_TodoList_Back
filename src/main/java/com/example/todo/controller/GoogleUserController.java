package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.googleDTO.GoogleInfResponse;
import com.example.todo.googleDTO.GoogleRequest;
import com.example.todo.googleDTO.GoogleResponse;
import com.example.todo.model.UserEntity;
import com.example.todo.persistence.UserRepository;
import com.example.todo.properties.GoogleClientProperties;
import com.example.todo.provider.AuthProvider;
import com.example.todo.security.TokenProvider;
import com.example.todo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleUserController {

    private final GoogleClientProperties googleClientProperties;
    private final UserService userService;
    private TokenProvider tokenProvider;

    // 테스트 변수 //////////////********
    @Autowired
    private UserRepository userRepository;
    ///////////////////////////********

    @Autowired
    public GoogleUserController(GoogleClientProperties googleClientProperties, UserService userService,  TokenProvider tokenProvider) {
        this.googleClientProperties = googleClientProperties;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @RequestMapping(value = "/api/v1/oauth2/google", method = RequestMethod.POST)
    public ResponseEntity<String> loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                + googleClientProperties.getId()
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google/callback&response_type=code&scope=email%20profile%20openid&access_type=offline";

        return ResponseEntity.ok().body(reqUrl);
    }

    @RequestMapping(value="/api/v1/oauth2/google/callback", method = RequestMethod.GET)
    public void loginGoogle(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws IOException {
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

        String jwtToken = resultEntity.getBody().getId_token();

        Map<String, String> map = new HashMap<>();
        map.put("id_token", jwtToken);
        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                map, GoogleInfResponse.class);

        String googleEmail = resultEntity2.getBody().getEmail();
        String name = resultEntity2.getBody().getName();

        // 테스트///////////////////////*********
        System.out.println("------------------------------------" + "\n" + "GoogleUserController.java");
        System.out.println("email: " + googleEmail);
        System.out.println("name: " + name);
        //테스트////////////////////////**********

        // 사용자 정보 저장
        UserEntity user = userService.saveOrUpdateGoogleUser(googleEmail, name);

        // 테스트///////////////////////*********
        UserEntity findUser = userRepository.findByEmail(user.getEmail());
        System.out.println("user email: " + findUser.getEmail());
        System.out.println("------------------------------------");
        //테스트////////////////////////**********

        user.setToken(jwtToken);

        // 응답 URL 생성
        String redirectUrl = "http://localhost:3000/callback?name="
                + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8.toString())
                + "&googleEmail=" + user.getEmail();

        // 클라이언트로 리디렉션
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping(value="/api/validGoogleToken", method = RequestMethod.POST)
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {

        System.out.println("checkToken");

        String googleEmail = request.get("email");
        String googleName = request.get("name");

        // 진행 상황 보고 코드 //
        System.out.println("name: " + googleName);
        ///////////////////////

        UserEntity user = userService.getByCredentials(googleEmail, "", null, AuthProvider.GOOGLE);
        System.out.println(user != null);

        if(user != null){
            final String token = tokenProvider.create(user);

            // 진행상황 보고 코드 //
            System.out.println("creat google Login User Token!");
            /////////////////////

            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .token(token)
                    .provider(AuthProvider.GOOGLE)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }
        else{
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
