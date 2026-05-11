package com.antrian.app.controller;

import com.antrian.app.dto.request.UpdateKonfigurasiRequest;
import com.antrian.app.dto.response.ApiResponse;
import com.antrian.app.entity.KonfigurasiAntrian;
import com.antrian.app.enums.JenisLayanan;
import com.antrian.app.service.KonfigurasiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/konfigurasi")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Konfigurasi Antrian", description = "API konfigurasi layanan antrian (Admin only)")
public class KonfigurasiController {

    private final KonfigurasiService konfigurasiService;

    @GetMapping
    @Operation(summary = "Lihat semua konfigurasi")
    public ResponseEntity<ApiResponse<List<KonfigurasiAntrian>>> getAllKonfigurasi() {
        return ResponseEntity.ok(ApiResponse.success(konfigurasiService.getAllKonfigurasi()));
    }

    @GetMapping("/{jenisLayanan}")
    @Operation(summary = "Konfigurasi per jenis layanan")
    public ResponseEntity<ApiResponse<KonfigurasiAntrian>> getKonfigurasi(
            @PathVariable JenisLayanan jenisLayanan) {
        return ResponseEntity.ok(ApiResponse.success(konfigurasiService.getKonfigurasi(jenisLayanan)));
    }

    @PutMapping("/{jenisLayanan}")
    @Operation(summary = "Update konfigurasi")
    public ResponseEntity<ApiResponse<KonfigurasiAntrian>> updateKonfigurasi(
            @PathVariable JenisLayanan jenisLayanan,
            @Valid @RequestBody UpdateKonfigurasiRequest request) {
        KonfigurasiAntrian updated = konfigurasiService.updateKonfigurasi(jenisLayanan, request);
        return ResponseEntity.ok(ApiResponse.success("Konfigurasi berhasil diupdate", updated));
    }
}
