package com.smartqueue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String serviceType;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String status;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
}
