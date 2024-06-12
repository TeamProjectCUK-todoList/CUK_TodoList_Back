package com.example.todo.googleDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleResponse {
    @JsonProperty("access_token")
    private String access_token; // 애플리케이션이 Google API 요청을 승인하기 위해 보내는 토큰

    @JsonProperty("expires_in")
    private Long expires_in;   // Access Token의 남은 수명

    @JsonProperty("refresh_token")
    private String refresh_token;    // 새 액세스 토큰을 얻는 데 사용할 수 있는 토큰

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("token_type")
    private String token_type;   // 반환된 토큰 유형(Bearer 고정)

    @JsonProperty("id_token")
    private String id_token;
}
