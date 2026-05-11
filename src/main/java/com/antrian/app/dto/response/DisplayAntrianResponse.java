package com.antrian.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayAntrianResponse {

    private LocalDate tanggal;

    // Pengaduan
    private AntrianDisplayInfo pengaduan;

    // Konsultasi
    private AntrianDisplayInfo konsultasi;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AntrianDisplayInfo {
        private String jenisLayanan;
        private String nomorSedangDilayani; // nomor antrian yang sedang dilayani
        private String nomorBerikutnya; // nomor antrian berikutnya
        private Integer totalMenunggu; // total antrian yang masih menunggu
        private Integer totalSelesai; // total antrian selesai hari ini
        private Integer totalHariIni; // total antrian masuk hari ini
        private Boolean sedangAktif; // apakah layanan sedang berjalan
    }
}
