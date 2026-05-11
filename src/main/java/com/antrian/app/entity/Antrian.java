package com.antrian.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "antrian", indexes = {
        @Index(name = "idx_antrian_tanggal", columnList = "tanggalAntrian"),
        @Index(name = "idx_antrian_jenis_status", columnList = "jenisLayanan, status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Antrian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String nomorAntrian;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JenisLayanan jenisLayanan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusAntrian status = StatusAntrian.MENUNGGU;

    @Column(nullable = false)
    private LocalDate tanggalAntrian;

    @Column(nullable = false)
    private Integer nomorUrut;

    // Nama pengunjung (opsional, boleh anonim)
    @Column(length = 100)
    private String namaPengunjung;

    @Column(length = 15)
    private String noHp;

    @Column(columnDefinition = "TEXT")
    private String keterangan;

    // Operator yang melayani
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    // Waktu dipanggil
    private LocalDateTime waktuDipanggil;

    // Waktu mulai dilayani
    private LocalDateTime waktuMulaiLayanan;

    // Waktu selesai
    private LocalDateTime waktuSelesai;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
