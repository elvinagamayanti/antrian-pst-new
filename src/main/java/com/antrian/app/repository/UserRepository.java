package com.antrian.app.repository;

import com.antrian.app.entity.User;
import com.antrian.app.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByAktif(Boolean aktif);

    List<User> findByRoleAndAktif(Role role, Boolean aktif);
}
