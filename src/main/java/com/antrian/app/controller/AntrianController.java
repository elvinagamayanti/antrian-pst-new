package com.antrian.app.controller;

import com.antrian.app.dto.request.AmbilAntrianRequest;
import com.antrian.app.dto.response.AntrianResponse;
import com.antrian.app.dto.response.ApiResponse;
import com.antrian.app.dto.response.DisplayAntrianResponse;
import com.antrian.app.dto.response.StatistikResponse;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.enums.StatusAntrian;
import com.antrian.app.service.AntrianService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Antrian", description = "API untuk sistem antrian")
public class AntrianController {

    private final AntrianService antrianService;

    // ================================
    // PUBLIC ENDPOINTS (no auth)
    // ================================

    @PostMapping("/antrian/ambil")
    @Operation(summary = "Ambil nomor antrian (public)", description = "Pengunjung mengambil nomor antrian")
    public ResponseEntity<ApiResponse<AntrianResponse>> ambilAntrian(
            @Valid @RequestBody AmbilAntrianRequest request) {
        AntrianResponse response = antrianService.ambilAntrian(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nomor antrian berhasil diambil", response));
    }

    @GetMapping("/antrian/cek/{nomorAntrian}")
    @Operation(summary = "Cek status antrian (public)")
    public ResponseEntity<ApiResponse<AntrianResponse>> cekAntrian(
            @PathVariable String nomorAntrian,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        return ResponseEntity.ok(ApiResponse.success(antrianService.cekAntrian(nomorAntrian, tanggal)));
    }

    @GetMapping("/display")
    @Operation(summary = "Display antrian untuk landing page (public)", description = "Menampilkan nomor antrian yang sedang dilayani untuk layar display")
    public ResponseEntity<ApiResponse<DisplayAntrianResponse>> getDisplayAntrian() {
        return ResponseEntity.ok(ApiResponse.success(antrianService.getDisplayAntrian()));
    }

    // ================================
    // OPERATOR / ADMIN ENDPOINTS
    // ================================

    @PostMapping("/antrian/panggil")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Panggil antrian berikutnya (Operator/Admin)")
    public ResponseEntity<ApiResponse<AntrianResponse>> panggilBerikutnya(
            @RequestParam JenisLayanan jenisLayanan,
            Authentication authentication) {
        AntrianResponse response = antrianService.panggilBerikutnya(
                jenisLayanan, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Antrian berhasil dipanggil", response));
    }

    @PatchMapping("/antrian/{id}/mulai")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mulai melayani antrian (Operator/Admin)")
    public ResponseEntity<ApiResponse<AntrianResponse>> mulaiLayani(
            @PathVariable Long id,
            Authentication authentication) {
        AntrianResponse response = antrianService.mulaiLayani(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Mulai melayani antrian", response));
    }

    @PatchMapping("/antrian/{id}/selesai")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Selesai melayani antrian (Operator/Admin)")
    public ResponseEntity<ApiResponse<AntrianResponse>> selesaiLayani(
            @PathVariable Long id,
            Authentication authentication) {
        AntrianResponse response = antrianService.selesaiLayani(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Antrian selesai dilayani", response));
    }

    @PatchMapping("/antrian/{id}/lewati")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Lewati/skip antrian (Operator/Admin)")
    public ResponseEntity<ApiResponse<AntrianResponse>> lewatiAntrian(
            @PathVariable Long id,
            Authentication authentication) {
        AntrianResponse response = antrianService.lewatiAntrian(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Antrian berhasil dilewati", response));
    }

    @PatchMapping("/antrian/{id}/panggil-ulang")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Panggil ulang antrian (Operator/Admin)")
    public ResponseEntity<ApiResponse<AntrianResponse>> panggilUlang(
            @PathVariable Long id,
            Authentication authentication) {
        AntrianResponse response = antrianService.panggilUlang(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Antrian berhasil dipanggil ulang", response));
    }

    @GetMapping("/antrian")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Daftar antrian hari ini (Operator/Admin)")
    public ResponseEntity<ApiResponse<List<AntrianResponse>>> getAntrianHariIni(
            @RequestParam(required = false) JenisLayanan jenisLayanan,
            @RequestParam(required = false) StatusAntrian status) {
        List<AntrianResponse> antrian = antrianService.getAntrianHariIni(jenisLayanan, status);
        return ResponseEntity.ok(ApiResponse.success(antrian));
    }

    @GetMapping("/antrian/history")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Riwayat antrian by tanggal (Admin)")
    public ResponseEntity<ApiResponse<List<AntrianResponse>>> getAntrianByTanggal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
            @RequestParam(required = false) JenisLayanan jenisLayanan) {
        List<AntrianResponse> antrian = antrianService.getAntrianByTanggal(tanggal, jenisLayanan);
        return ResponseEntity.ok(ApiResponse.success(antrian));
    }

    @GetMapping("/statistik")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Statistik antrian harian (Admin)")
    public ResponseEntity<ApiResponse<StatistikResponse>> getStatistik(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        LocalDate tgl = tanggal != null ? tanggal : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(antrianService.getStatistikHarian(tgl)));
    }
}
