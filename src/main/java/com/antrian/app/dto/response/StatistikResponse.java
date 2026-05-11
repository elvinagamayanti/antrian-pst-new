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
public class StatistikResponse {

    private LocalDate tanggal;

    // Pengaduan
    private Long pengaduanMenunggu;
    private Long pengaduanDilayani;
    private Long pengaduanSelesai;
    private Long pengaduanDilewati;
    private Long pengaduanTotal;

    // Konsultasi
    private Long konsultasiMenunggu;
    private Long konsultasiDilayani;
    private Long konsultasiSelesai;
    private Long konsultasiDilewati;
    private Long konsultasiTotal;

    // Grand total
    private Long grandTotal;
}
