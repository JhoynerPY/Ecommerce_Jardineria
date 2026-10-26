package com.uniquindio.ecommerce.infrastructure.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String nombre;
    private List<String> roles;

    @Builder.Default
    private String tipoToken = "Bearer";
}