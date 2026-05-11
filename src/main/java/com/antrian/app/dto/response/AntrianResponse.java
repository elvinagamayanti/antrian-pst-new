package com.antrian.app.dto.response;

import com.antrian.app.entity.Antrian;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AntrianResponse {

    private Long id;
    private String nomorAntrian;
    private JenisLayanan jenisLayanan;
    private StatusAntrian status;
    private LocalDate tanggalAntrian;
    private Integer nomorUrut;
    private String namaPengunjung;
    private String noHp;
    private String keterangan;
    private String operatorNama;
    private LocalDateTime waktuDipanggil;
    private LocalDateTime waktuMulaiLayanan;
    private LocalDateTime waktuSelesai;
    private LocalDateTime createdAt;

    // Estimasi waktu tunggu (menit)
    private Integer estimasiTunggu;

    public static AntrianResponse fromEntity(Antrian antrian) {
        return AntrianResponse.builder()
                .id(antrian.getId())
                .nomorAntrian(antrian.getNomorAntrian())
                .jenisLayanan(antrian.getJenisLayanan())
                .status(antrian.getStatus())
                .tanggalAntrian(antrian.getTanggalAntrian())
                .nomorUrut(antrian.getNomorUrut())
                .namaPengunjung(antrian.getNamaPengunjung())
                .noHp(antrian.getNoHp())
                .keterangan(antrian.getKeterangan())
                .operatorNama(antrian.getOperator() != null ? antrian.getOperator().getNamaLengkap() : null)
                .waktuDipanggil(antrian.getWaktuDipanggil())
                .waktuMulaiLayanan(antrian.getWaktuMulaiLayanan())
                .waktuSelesai(antrian.getWaktuSelesai())
                .createdAt(antrian.getCreatedAt())
                .build();
    }
}
