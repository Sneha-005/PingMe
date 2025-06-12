package com.devsneha.pingme.model.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String phoneNumber;
    private boolean enabled;
} 