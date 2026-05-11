package com.antrian.app.entity;

import com.antrian.app.enums.JenisLayanan;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "konfigurasi_antrian")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KonfigurasiAntrian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private JenisLayanan jenisLayanan;

    @Column(nullable = false, length = 5)
    private String prefix;

    @Column(nullable = false)
    @Builder.Default
    private Integer kapasitasPerHari = 100;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aktif = true;

    // Counter antrian hari ini (reset setiap hari)
    @Column(nullable = false)
    @Builder.Default
    private Integer counterHariIni = 0;
}
