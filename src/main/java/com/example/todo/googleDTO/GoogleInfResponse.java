package com.example.todo.googleDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleInfResponse {
    @JsonProperty("iss")
    private String iss;

    @JsonProperty("azp")
    private String azp;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private String emailVerified;

    @JsonProperty("at_hash")
    private String atHash;

    @JsonProperty("name")
    private String name;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("iat")
    private String iat;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("alg")
    private String alg;

    @JsonProperty("kid")
    private String kid;

    @JsonProperty("typ")
    private String typ;
}
