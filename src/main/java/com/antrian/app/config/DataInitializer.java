package com.antrian.app.config;

import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.entity.User;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.Role;
import com.antrian.app.repository.KonfigurasiAntrianRepository;
import com.antrian.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final KonfigurasiAntrianRepository konfigurasiRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initUsers();
            initKonfigurasi();
        };
    }

    private void initUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .namaLengkap("Administrator")
                    .email("admin@antrian.com")
                    .role(Role.ADMIN)
                    .aktif(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin default dibuat: username=admin, password=admin123");
        }

        if (!userRepository.existsByUsername("operator1")) {
            User operator = User.builder()
                    .username("operator1")
                    .password(passwordEncoder.encode("operator123"))
                    .namaLengkap("Operator Pengaduan")
                    .email("operator1@antrian.com")
                    .role(Role.OPERATOR)
                    .aktif(true)
                    .build();
            userRepository.save(operator);
            log.info("✅ Operator default dibuat: username=operator1, password=operator123");
        }

        if (!userRepository.existsByUsername("operator2")) {
            User operator2 = User.builder()
                    .username("operator2")
                    .password(passwordEncoder.encode("operator123"))
                    .namaLengkap("Operator Konsultasi")
                    .email("operator2@antrian.com")
                    .role(Role.OPERATOR)
                    .aktif(true)
                    .build();
            userRepository.save(operator2);
            log.info("✅ Operator2 default dibuat: username=operator2, password=operator123");
        }
    }

    private void initKonfigurasi() {
        if (konfigurasiRepository.findByJenisLayanan(JenisLayanan.PENGADUAN).isEmpty()) {
            KonfigurasiAntrian pengaduan = KonfigurasiAntrian.builder()
                    .jenisLayanan(JenisLayanan.PENGADUAN)
                    .prefix("P")
                    .kapasitasPerHari(100)
                    .aktif(true)
                    .counterHariIni(0)
                    .build();
            konfigurasiRepository.save(pengaduan);
            log.info("✅ Konfigurasi PENGADUAN diinisialisasi");
        }

        if (konfigurasiRepository.findByJenisLayanan(JenisLayanan.KONSULTASI).isEmpty()) {
            KonfigurasiAntrian konsultasi = KonfigurasiAntrian.builder()
                    .jenisLayanan(JenisLayanan.KONSULTASI)
                    .prefix("K")
                    .kapasitasPerHari(100)
                    .aktif(true)
                    .counterHariIni(0)
                    .build();
            konfigurasiRepository.save(konsultasi);
            log.info("✅ Konfigurasi KONSULTASI diinisialisasi");
        }
    }
}
