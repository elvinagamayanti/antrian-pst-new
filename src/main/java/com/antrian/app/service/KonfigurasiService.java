package com.antrian.app.service;

import com.antrian.app.dto.request.UpdateKonfigurasiRequest;
import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.enums.JenisLayanan;

import java.util.List;

public interface KonfigurasiService {
    List<KonfigurasiAntrian> getAllKonfigurasi();

    KonfigurasiAntrian getKonfigurasi(JenisLayanan jenisLayanan);

    KonfigurasiAntrian updateKonfigurasi(JenisLayanan jenisLayanan, UpdateKonfigurasiRequest request);
}
