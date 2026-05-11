package com.antrian.app.service;

import com.antrian.app.dto.request.CreateUserRequest;
import com.antrian.app.dto.request.UpdateUserRequest;
import com.antrian.app.dto.response.UserResponse;
import com.antrian.app.enums.Role;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(Role role);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    void toggleAktif(Long id);

    String generateUsername(String namaLengkap);
}
