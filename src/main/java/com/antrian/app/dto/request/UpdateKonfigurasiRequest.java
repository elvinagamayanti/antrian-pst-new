package com.antrian.app.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateKonfigurasiRequest {

    @Min(value = 1, message = "Kapasitas minimal 1")
    private Integer kapasitasPerHari;

    private Boolean aktif;
}
