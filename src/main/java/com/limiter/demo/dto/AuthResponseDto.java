package com.limiter.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthResponseDto implements Serializable {
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

}
