package com.jwtImplementation.jwt_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private  String accessToken;
    private String refreshToken;
    private String message;
    private Integer statusCode;
    private String accessTokenExpiration; // Expiration time for access token
    private  String refreshTokenExpiration;
    //Constructor for tokens only
    public AuthResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    // Constructor with CustomResponse
    public AuthResponse(String message, Integer statusCode, String accessToken, String refreshToken, String accessTokenExpiration, String refreshTokenExpiration) {
        this.message = message;
        this.statusCode = statusCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

    }
}
