package com.example.todo.controller;

import com.example.todo.properties.GoogleClientProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
