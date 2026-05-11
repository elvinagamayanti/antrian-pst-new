package com.antrian.app.service.impl;

import com.antrian.app.dto.request.CreateUserRequest;
import com.antrian.app.dto.request.UpdateUserRequest;
import com.antrian.app.dto.response.UserResponse;
import com.antrian.app.entity.User;
import com.antrian.app.enums.Role;
import com.antrian.app.exception.BadRequestException;
import com.antrian.app.exception.ResourceNotFoundException;
import com.antrian.app.repository.UserRepository;
import com.antrian.app.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' sudah digunakan");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .namaLengkap(request.getNamaLengkap())
                .email(request.getEmail())
                .role(request.getRole())
                .aktif(true)
                .build();

        user = userRepository.save(user);
        log.info("User baru dibuat: {} dengan role {}", user.getUsername(), user.getRole());
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan: " + username));
        return UserResponse.fromEntity(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan id: " + id));

        if (request.getNamaLengkap() != null) {
            user.setNamaLengkap(request.getNamaLengkap());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getAktif() != null) {
            user.setAktif(request.getAktif());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        log.info("User diupdate: {}", user.getUsername());
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan id: " + id));
        userRepository.delete(user);
        log.info("User dihapus: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void toggleAktif(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan id: " + id));
        user.setAktif(!user.getAktif());
        userRepository.save(user);
        log.info("User {} status aktif diubah menjadi: {}", user.getUsername(), user.getAktif());
    }

    @Override
    public String generateUsername(String namaLengkap) {
        // Ubah nama ke lowercase, hapus karakter spesial, ganti spasi dengan titik
        String normalized = Normalizer.normalize(namaLengkap, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String base = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", ".");

        // Truncate jika terlalu panjang
        if (base.length() > 40) {
            base = base.substring(0, 40);
        }

        // Pastikan unik
        String candidate = base;
        int counter = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + counter;
            counter++;
        }

        return candidate;
    }
}
