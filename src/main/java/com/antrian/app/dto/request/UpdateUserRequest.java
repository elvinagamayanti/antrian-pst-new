package com.antrian.app.dto.request;

import com.antrian.app.enums.Role;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password; // nullable, kalau null berarti tidak diubah

    @Size(max = 100)
    private String namaLengkap;

    private String email;

    private Role role;

    private Boolean aktif;
}
