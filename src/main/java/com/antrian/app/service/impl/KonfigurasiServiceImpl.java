package com.antrian.app.service.impl;

import com.antrian.app.dto.request.UpdateKonfigurasiRequest;
import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.exception.ResourceNotFoundException;
import com.antrian.app.repository.KonfigurasiAntrianRepository;
import com.antrian.app.service.KonfigurasiService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KonfigurasiServiceImpl implements KonfigurasiService {

    private final KonfigurasiAntrianRepository konfigurasiRepository;

    @Override
    public List<KonfigurasiAntrian> getAllKonfigurasi() {
        return konfigurasiRepository.findAll();
    }

    @Override
    public KonfigurasiAntrian getKonfigurasi(JenisLayanan jenisLayanan) {
        return konfigurasiRepository.findByJenisLayanan(jenisLayanan)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Konfigurasi tidak ditemukan untuk: " + jenisLayanan));
    }

    @Override
    @Transactional
    public KonfigurasiAntrian updateKonfigurasi(JenisLayanan jenisLayanan,
            UpdateKonfigurasiRequest request) {
        KonfigurasiAntrian config = getKonfigurasi(jenisLayanan);

        if (request.getKapasitasPerHari() != null) {
            config.setKapasitasPerHari(request.getKapasitasPerHari());
        }
        if (request.getAktif() != null) {
            config.setAktif(request.getAktif());
        }

        return konfigurasiRepository.save(config);
    }
}
