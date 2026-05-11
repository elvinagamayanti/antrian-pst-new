package com.antrian.app.controller;

import com.antrian.app.dto.request.LoginRequest;
import com.antrian.app.dto.response.ApiResponse;
import com.antrian.app.dto.response.LoginResponse;
import com.antrian.app.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autentikasi", description = "API untuk login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login admin/operator", description = "Login menggunakan username dan password")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout admin/operator", description = "Logout pengguna dari sistem. Membutuhkan Bearer token.")
    public ResponseEntity<ApiResponse<Void>> logout(org.springframework.security.core.Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "Unknown";
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout berhasil untuk user: " + username, null));
    }
}
