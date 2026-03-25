package com.smartqueue.dto;

import com.smartqueue.model.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueDTO {
    private Long id;
    private Long appointmentId;
    private String userName;
    private String serviceType;
    private LocalDate appointmentDate;
    private String timeSlot;
    private Integer queueNumber;
    private QueueStatus queueStatus;
}
