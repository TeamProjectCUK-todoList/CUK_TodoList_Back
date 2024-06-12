package com.example.todo.controller;

import com.example.todo.googleDTO.GoogleInfResponse;
import com.example.todo.googleDTO.GoogleRequest;
import com.example.todo.googleDTO.GoogleResponse;
import com.example.todo.properties.GoogleClientProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleLoginController {

    private final GoogleClientProperties googleClientProperties;

    public GoogleLoginController(GoogleClientProperties googleClientProperties) {
        this.googleClientProperties = googleClientProperties;
    }

    @RequestMapping(value = "/api/v1/oauth2/google", method = RequestMethod.POST)
    public String loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                + googleClientProperties.getId()
                + "&redirect_uri=http://localhost:8080/api/v1/oauth2/google/callback&response_type=code&scope=email%20profile%20openid&access_type=offline";

        return reqUrl;
    }

    @RequestMapping(value="/api/v1/oauth2/google/callback", method = RequestMethod.GET)
    public String loginGoogle(@RequestParam(value = "code") String authCode){
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
        String email=resultEntity2.getBody().getEmail();
        return email;
    }
}
