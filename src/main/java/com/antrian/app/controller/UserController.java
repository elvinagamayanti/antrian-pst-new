package com.antrian.app.controller;

import com.antrian.app.dto.request.CreateUserRequest;
import com.antrian.app.dto.request.UpdateUserRequest;
import com.antrian.app.dto.response.ApiResponse;
import com.antrian.app.dto.response.UserResponse;
import com.antrian.app.enums.Role;
import com.antrian.app.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manajemen User", description = "API untuk manajemen akun user (Admin only)")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Buat user baru", description = "Generate akun baru untuk admin/operator")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User berhasil dibuat", user));
    }

    @GetMapping
    @Operation(summary = "Daftar semua user")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role) {
        List<UserResponse> users = role != null
                ? userService.getUsersByRole(role)
                : userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update data user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User berhasil diupdate", user));
    }

    @PatchMapping("/{id}/toggle-aktif")
    @Operation(summary = "Aktifkan/nonaktifkan user")
    public ResponseEntity<ApiResponse<Void>> toggleAktif(@PathVariable Long id) {
        userService.toggleAktif(id);
        return ResponseEntity.ok(ApiResponse.success("Status aktif user berhasil diubah", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User berhasil dihapus", null));
    }

    @GetMapping("/generate-username")
    @Operation(summary = "Generate username dari nama lengkap")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateUsername(
            @RequestParam String namaLengkap) {
        String username = userService.generateUsername(namaLengkap);
        return ResponseEntity.ok(ApiResponse.success(Map.of("username", username)));
    }
}
