package com.keanhive.stich.api.integration.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserTokenResponse {

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("id_token")
    public String idToken;

    @JsonProperty("refresh_token")
    public String refreshToken;

    public String scope;
}
