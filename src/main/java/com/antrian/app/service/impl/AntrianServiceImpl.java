package com.antrian.app.service.impl;

import com.antrian.app.dto.request.AmbilAntrianRequest;
import com.antrian.app.dto.response.AntrianResponse;
import com.antrian.app.dto.response.DisplayAntrianResponse;
import com.antrian.app.dto.response.StatistikResponse;
import com.antrian.app.entity.Antrian;
import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.entity.User;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;
import com.antrian.app.exception.BadRequestException;
import com.antrian.app.exception.ResourceNotFoundException;
import com.antrian.app.repository.AntrianRepository;
import com.antrian.app.repository.KonfigurasiAntrianRepository;
import com.antrian.app.repository.UserRepository;
import com.antrian.app.service.AntrianService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AntrianServiceImpl implements AntrianService {

    private final AntrianRepository antrianRepository;
    private final KonfigurasiAntrianRepository konfigurasiRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AntrianResponse ambilAntrian(AmbilAntrianRequest request) {
        KonfigurasiAntrian config = konfigurasiRepository
                .findByJenisLayanan(request.getJenisLayanan())
                .orElseThrow(() -> new BadRequestException("Konfigurasi layanan tidak ditemukan"));

        if (!config.getAktif()) {
            throw new BadRequestException("Layanan " + request.getJenisLayanan().name().toLowerCase()
                    + " sedang tidak aktif");
        }

        LocalDate today = LocalDate.now();
        long totalHariIni = antrianRepository.countByTanggalAntrianAndJenisLayanan(
                today, request.getJenisLayanan());

        if (totalHariIni >= config.getKapasitasPerHari()) {
            throw new BadRequestException("Kuota antrian " + request.getJenisLayanan().name().toLowerCase()
                    + " hari ini sudah penuh (" + config.getKapasitasPerHari() + " antrian)");
        }

        // Generate nomor urut
        int nomorUrut = config.getCounterHariIni() + 1;
        config.setCounterHariIni(nomorUrut);
        konfigurasiRepository.save(config);

        // Generate nomor antrian: PREFIX + 3 digit (e.g. P001, K012)
        String nomorAntrian = String.format("%s%03d", config.getPrefix(), nomorUrut);

        Antrian antrian = Antrian.builder()
                .nomorAntrian(nomorAntrian)
                .jenisLayanan(request.getJenisLayanan())
                .status(StatusAntrian.MENUNGGU)
                .tanggalAntrian(today)
                .nomorUrut(nomorUrut)
                .namaPengunjung(request.getNamaPengunjung())
                .noHp(request.getNoHp())
                .keterangan(request.getKeterangan())
                .build();

        antrian = antrianRepository.save(antrian);
        log.info("Antrian baru: {} - {}", antrian.getNomorAntrian(), antrian.getJenisLayanan());

        // Hitung posisi dan estimasi tunggu
        AntrianResponse response = AntrianResponse.fromEntity(antrian);
        long posisi = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                today, request.getJenisLayanan(), StatusAntrian.MENUNGGU);
        response.setEstimasiTunggu((int) posisi * 5); // Estimasi 5 menit per antrian

        return response;
    }

    @Override
    public AntrianResponse cekAntrian(String nomorAntrian, LocalDate tanggal) {
        LocalDate tgl = tanggal != null ? tanggal : LocalDate.now();
        Antrian antrian = antrianRepository
                .findAll().stream()
                .filter(a -> a.getNomorAntrian().equalsIgnoreCase(nomorAntrian)
                        && a.getTanggalAntrian().equals(tgl))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Antrian " + nomorAntrian + " tidak ditemukan"));

        AntrianResponse response = AntrianResponse.fromEntity(antrian);

        // Hitung posisi tunggu
        if (antrian.getStatus() == StatusAntrian.MENUNGGU) {
            long posisi = antrianRepository
                    .findByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
                            tgl, antrian.getJenisLayanan(), StatusAntrian.MENUNGGU)
                    .stream()
                    .filter(a -> a.getNomorUrut() <= antrian.getNomorUrut())
                    .count();
            response.setEstimasiTunggu((int) posisi * 5);
        }

        return response;
    }

    @Override
    public DisplayAntrianResponse getDisplayAntrian() {
        LocalDate today = LocalDate.now();

        return DisplayAntrianResponse.builder()
                .tanggal(today)
                .pengaduan(buildDisplayInfo(today, JenisLayanan.PENGADUAN))
                .konsultasi(buildDisplayInfo(today, JenisLayanan.KONSULTASI))
                .build();
    }

    private DisplayAntrianResponse.AntrianDisplayInfo buildDisplayInfo(
            LocalDate tanggal, JenisLayanan jenisLayanan) {

        // Cari antrian yang sedang dilayani
        List<Antrian> aktif = antrianRepository.findAntrianAktif(tanggal, jenisLayanan);
        String sedangDilayani = aktif.stream()
                .filter(a -> a.getStatus() == StatusAntrian.DILAYANI || a.getStatus() == StatusAntrian.DIPANGGIL)
                .map(Antrian::getNomorAntrian)
                .findFirst()
                .orElse("-");

        // Berikutnya yang menunggu
        Optional<Antrian> berikutnya = antrianRepository
                .findFirstByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
                        tanggal, jenisLayanan, StatusAntrian.MENUNGGU);

        long totalMenunggu = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                tanggal, jenisLayanan, StatusAntrian.MENUNGGU);
        long totalSelesai = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                tanggal, jenisLayanan, StatusAntrian.SELESAI);
        long totalHariIni = antrianRepository.countByTanggalAntrianAndJenisLayanan(
                tanggal, jenisLayanan);

        boolean aktifLayanan = konfigurasiRepository
                .findByJenisLayanan(jenisLayanan)
                .map(KonfigurasiAntrian::getAktif)
                .orElse(false);

        return DisplayAntrianResponse.AntrianDisplayInfo.builder()
                .jenisLayanan(jenisLayanan.name())
                .nomorSedangDilayani(sedangDilayani)
                .nomorBerikutnya(berikutnya.map(Antrian::getNomorAntrian).orElse("-"))
                .totalMenunggu((int) totalMenunggu)
                .totalSelesai((int) totalSelesai)
                .totalHariIni((int) totalHariIni)
                .sedangAktif(aktifLayanan)
                .build();
    }

    @Override
    @Transactional
    public AntrianResponse panggilBerikutnya(JenisLayanan jenisLayanan, String usernameOperator) {
        LocalDate today = LocalDate.now();
        User operator = getOperator(usernameOperator);

        // Cek apakah masih ada yang sedang dipanggil/dilayani
        List<Antrian> masihAktif = antrianRepository.findAntrianAktif(today, jenisLayanan);
        if (!masihAktif.isEmpty()) {
            throw new BadRequestException("Masih ada antrian yang belum selesai. " +
                    "Selesaikan atau lewati terlebih dahulu.");
        }

        // Cari antrian berikutnya yang MENUNGGU
        Antrian berikutnya = antrianRepository
                .findFirstByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
                        today, jenisLayanan, StatusAntrian.MENUNGGU)
                .orElseThrow(() -> new BadRequestException(
                        "Tidak ada antrian " + jenisLayanan.name().toLowerCase() + " yang menunggu"));

        berikutnya.setStatus(StatusAntrian.DIPANGGIL);
        berikutnya.setOperator(operator);
        berikutnya.setWaktuDipanggil(LocalDateTime.now());
        berikutnya = antrianRepository.save(berikutnya);

        log.info("Antrian dipanggil: {} oleh {}", berikutnya.getNomorAntrian(), usernameOperator);
        return AntrianResponse.fromEntity(berikutnya);
    }

    @Override
    @Transactional
    public AntrianResponse mulaiLayani(Long antrianId, String usernameOperator) {
        Antrian antrian = getAntrianById(antrianId);
        User operator = getOperator(usernameOperator);

        if (antrian.getStatus() != StatusAntrian.DIPANGGIL) {
            throw new BadRequestException("Antrian harus dalam status DIPANGGIL untuk mulai dilayani");
        }

        antrian.setStatus(StatusAntrian.DILAYANI);
        antrian.setOperator(operator);
        antrian.setWaktuMulaiLayanan(LocalDateTime.now());
        antrian = antrianRepository.save(antrian);

        log.info("Mulai layani antrian: {} oleh {}", antrian.getNomorAntrian(), usernameOperator);
        return AntrianResponse.fromEntity(antrian);
    }

    @Override
    @Transactional
    public AntrianResponse selesaiLayani(Long antrianId, String usernameOperator) {
        Antrian antrian = getAntrianById(antrianId);

        if (antrian.getStatus() != StatusAntrian.DILAYANI && antrian.getStatus() != StatusAntrian.DIPANGGIL) {
            throw new BadRequestException("Antrian harus dalam status DIPANGGIL atau DILAYANI");
        }

        antrian.setStatus(StatusAntrian.SELESAI);
        antrian.setWaktuSelesai(LocalDateTime.now());
        antrian = antrianRepository.save(antrian);

        log.info("Selesai layani antrian: {} oleh {}", antrian.getNomorAntrian(), usernameOperator);
        return AntrianResponse.fromEntity(antrian);
    }

    @Override
    @Transactional
    public AntrianResponse lewatiAntrian(Long antrianId, String usernameOperator) {
        Antrian antrian = getAntrianById(antrianId);

        if (antrian.getStatus() != StatusAntrian.MENUNGGU && antrian.getStatus() != StatusAntrian.DIPANGGIL) {
            throw new BadRequestException("Hanya antrian yang MENUNGGU atau DIPANGGIL yang bisa dilewati");
        }

        antrian.setStatus(StatusAntrian.DILEWATI);
        antrian.setWaktuSelesai(LocalDateTime.now());
        antrian = antrianRepository.save(antrian);

        log.info("Antrian dilewati: {} oleh {}", antrian.getNomorAntrian(), usernameOperator);
        return AntrianResponse.fromEntity(antrian);
    }

    @Override
    @Transactional
    public AntrianResponse panggilUlang(Long antrianId, String usernameOperator) {
        Antrian antrian = getAntrianById(antrianId);

        if (antrian.getStatus() != StatusAntrian.DILEWATI && antrian.getStatus() != StatusAntrian.DIPANGGIL) {
            throw new BadRequestException("Hanya antrian DILEWATI atau DIPANGGIL yang bisa dipanggil ulang");
        }

        antrian.setStatus(StatusAntrian.DIPANGGIL);
        antrian.setWaktuDipanggil(LocalDateTime.now());
        antrian = antrianRepository.save(antrian);

        log.info("Antrian dipanggil ulang: {} oleh {}", antrian.getNomorAntrian(), usernameOperator);
        return AntrianResponse.fromEntity(antrian);
    }

    @Override
    public List<AntrianResponse> getAntrianHariIni(JenisLayanan jenisLayanan, StatusAntrian status) {
        LocalDate today = LocalDate.now();
        List<Antrian> result;

        if (jenisLayanan != null && status != null) {
            result = antrianRepository.findByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
                    today, jenisLayanan, status);
        } else if (jenisLayanan != null) {
            result = antrianRepository.findByTanggalAntrianAndJenisLayananOrderByNomorUrut(
                    today, jenisLayanan);
        } else if (status != null) {
            result = antrianRepository.findByTanggalAntrianAndStatusOrderByNomorUrut(today, status);
        } else {
            result = antrianRepository.findByTanggalAntrianOrderByNomorUrut(today);
        }

        return result.stream().map(AntrianResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AntrianResponse> getAntrianByTanggal(LocalDate tanggal, JenisLayanan jenisLayanan) {
        List<Antrian> result;
        if (jenisLayanan != null) {
            result = antrianRepository.findByTanggalAntrianAndJenisLayananOrderByNomorUrut(
                    tanggal, jenisLayanan);
        } else {
            result = antrianRepository.findByTanggalAntrianOrderByNomorUrut(tanggal);
        }
        return result.stream().map(AntrianResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public StatistikResponse getStatistikHarian(LocalDate tanggal) {
        StatistikResponse.StatistikResponseBuilder builder = StatistikResponse.builder().tanggal(tanggal);

        for (JenisLayanan jenis : JenisLayanan.values()) {
            long menunggu = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                    tanggal, jenis, StatusAntrian.MENUNGGU);
            long dilayani = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                    tanggal, jenis, StatusAntrian.DILAYANI) +
                    antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                            tanggal, jenis, StatusAntrian.DIPANGGIL);
            long selesai = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                    tanggal, jenis, StatusAntrian.SELESAI);
            long dilewati = antrianRepository.countByTanggalAntrianAndJenisLayananAndStatus(
                    tanggal, jenis, StatusAntrian.DILEWATI);
            long total = antrianRepository.countByTanggalAntrianAndJenisLayanan(tanggal, jenis);

            if (jenis == JenisLayanan.PENGADUAN) {
                builder.pengaduanMenunggu(menunggu)
                        .pengaduanDilayani(dilayani)
                        .pengaduanSelesai(selesai)
                        .pengaduanDilewati(dilewati)
                        .pengaduanTotal(total);
            } else {
                builder.konsultasiMenunggu(menunggu)
                        .konsultasiDilayani(dilayani)
                        .konsultasiSelesai(selesai)
                        .konsultasiDilewati(dilewati)
                        .konsultasiTotal(total);
            }
        }

        StatistikResponse stat = builder.build();
        long pengTotal = stat.getPengaduanTotal() != null ? stat.getPengaduanTotal() : 0;
        long konsTotal = stat.getKonsultasiTotal() != null ? stat.getKonsultasiTotal() : 0;
        stat.setGrandTotal(pengTotal + konsTotal);

        return stat;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Setiap tengah malam
    @Transactional
    public void resetCounterHarian() {
        log.info("Reset counter antrian harian dimulai...");
        List<KonfigurasiAntrian> configs = konfigurasiRepository.findAll();
        configs.forEach(config -> config.setCounterHariIni(0));
        konfigurasiRepository.saveAll(configs);
        log.info("Counter antrian berhasil direset untuk {} layanan", configs.size());
    }

    // Helper methods
    private Antrian getAntrianById(Long id) {
        return antrianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Antrian tidak ditemukan dengan id: " + id));
    }

    private User getOperator(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Operator tidak ditemukan: " + username));
    }
}
