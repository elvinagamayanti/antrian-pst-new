package com.antrian.app.dto.response;

import com.antrian.app.entity.User;
import com.antrian.app.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String namaLengkap;
    private String email;
    private Role role;
    private Boolean aktif;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .namaLengkap(user.getNamaLengkap())
                .email(user.getEmail())
                .role(user.getRole())
                .aktif(user.getAktif())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
