package com.antrian.app.service;

import com.antrian.app.dto.request.AmbilAntrianRequest;
import com.antrian.app.dto.response.AntrianResponse;
import com.antrian.app.dto.response.DisplayAntrianResponse;
import com.antrian.app.dto.response.StatistikResponse;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;

import java.time.LocalDate;
import java.util.List;

public interface AntrianService {

    // Public: ambil nomor antrian
    AntrianResponse ambilAntrian(AmbilAntrianRequest request);

    // Public: cek status antrian by nomor
    AntrianResponse cekAntrian(String nomorAntrian, LocalDate tanggal);

    // Public: display antrian untuk landing page
    DisplayAntrianResponse getDisplayAntrian();

    // Operator/Admin: panggil antrian berikutnya
    AntrianResponse panggilBerikutnya(JenisLayanan jenisLayanan, String usernameOperator);

    // Operator/Admin: mulai layani (konfirmasi hadir)
    AntrianResponse mulaiLayani(Long antrianId, String usernameOperator);

    // Operator/Admin: selesai layani
    AntrianResponse selesaiLayani(Long antrianId, String usernameOperator);

    // Operator/Admin: skip / lewati antrian
    AntrianResponse lewatiAntrian(Long antrianId, String usernameOperator);

    // Admin: panggil ulang antrian
    AntrianResponse panggilUlang(Long antrianId, String usernameOperator);

    // Admin: get semua antrian hari ini
    List<AntrianResponse> getAntrianHariIni(JenisLayanan jenisLayanan, StatusAntrian status);

    // Admin: get antrian by tanggal
    List<AntrianResponse> getAntrianByTanggal(LocalDate tanggal, JenisLayanan jenisLayanan);

    // Admin: statistik harian
    StatistikResponse getStatistikHarian(LocalDate tanggal);

    // Scheduled: reset counter harian
    void resetCounterHarian();
}
