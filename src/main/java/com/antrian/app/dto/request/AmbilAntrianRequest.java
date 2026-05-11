package com.antrian.app.dto.request;

import com.antrian.app.enums.JenisLayanan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AmbilAntrianRequest {

    @NotNull(message = "Jenis layanan tidak boleh kosong")
    private JenisLayanan jenisLayanan;

    @Size(max = 100)
    private String namaPengunjung; // opsional

    @Size(max = 15)
    private String noHp; // opsional

    @Size(max = 500)
    private String keterangan; // opsional
}
