package com.smartqueue.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookAppointmentRequest {

    @NotBlank
    private String serviceType;

    @NotNull
    @FutureOrPresent
    private LocalDate appointmentDate;

    @NotBlank
    private String timeSlot;

    private Long doctorId;
}
