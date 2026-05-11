package com.antrian.app.dto.request;

import com.antrian.app.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, max = 50, message = "Username harus 4-50 karakter")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(max = 100)
    private String namaLengkap;

    private String email;

    @NotNull(message = "Role tidak boleh kosong")
    private Role role;
}
