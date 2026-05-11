package com.antrian.app.repository;

import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.enums.JenisLayanan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KonfigurasiAntrianRepository extends JpaRepository<KonfigurasiAntrian, Long> {

    Optional<KonfigurasiAntrian> findByJenisLayanan(JenisLayanan jenisLayanan);
}
