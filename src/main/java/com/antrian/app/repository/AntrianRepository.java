package com.antrian.app.repository;

import com.antrian.app.entity.Antrian;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AntrianRepository extends JpaRepository<Antrian, Long> {

    List<Antrian> findByTanggalAntrianOrderByNomorUrut(LocalDate tanggal);

    List<Antrian> findByTanggalAntrianAndJenisLayananOrderByNomorUrut(
            LocalDate tanggal, JenisLayanan jenisLayanan);

    List<Antrian> findByTanggalAntrianAndStatusOrderByNomorUrut(
            LocalDate tanggal, StatusAntrian status);

    List<Antrian> findByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
            LocalDate tanggal, JenisLayanan jenisLayanan, StatusAntrian status);

    // Cari antrian yang sedang dipanggil/dilayani
    Optional<Antrian> findByTanggalAntrianAndJenisLayananAndStatusIn(
            LocalDate tanggal, JenisLayanan jenisLayanan, List<StatusAntrian> statuses);

    // Antrian berikutnya yang menunggu
    Optional<Antrian> findFirstByTanggalAntrianAndJenisLayananAndStatusOrderByNomorUrut(
            LocalDate tanggal, JenisLayanan jenisLayanan, StatusAntrian status);

    // Cek nomor antrian sudah ada belum
    boolean existsByNomorAntrianAndTanggalAntrian(String nomorAntrian, LocalDate tanggal);

    // Count per status per hari
    long countByTanggalAntrianAndJenisLayananAndStatus(
            LocalDate tanggal, JenisLayanan jenisLayanan, StatusAntrian status);

    long countByTanggalAntrianAndJenisLayanan(LocalDate tanggal, JenisLayanan jenisLayanan);

    // Statistik harian
    @Query("SELECT a.jenisLayanan, a.status, COUNT(a) FROM Antrian a " +
           "WHERE a.tanggalAntrian = :tanggal GROUP BY a.jenisLayanan, a.status")
    List<Object[]> countGroupByJenisAndStatus(@Param("tanggal") LocalDate tanggal);

    // Antrian yang sedang aktif (dipanggil atau dilayani) per jenis layanan
    @Query("SELECT a FROM Antrian a WHERE a.tanggalAntrian = :tanggal " +
           "AND a.jenisLayanan = :jenis AND a.status IN ('DIPANGGIL', 'DILAYANI') " +
           "ORDER BY a.nomorUrut")
    List<Antrian> findAntrianAktif(@Param("tanggal") LocalDate tanggal,
                                    @Param("jenis") JenisLayanan jenis);

    // Nomor urut terakhir per jenis per hari
    @Query("SELECT MAX(a.nomorUrut) FROM Antrian a " +
           "WHERE a.tanggalAntrian = :tanggal AND a.jenisLayanan = :jenis")
    Optional<Integer> findMaxNomorUrut(@Param("tanggal") LocalDate tanggal,
                                        @Param("jenis") JenisLayanan jenis);
}

