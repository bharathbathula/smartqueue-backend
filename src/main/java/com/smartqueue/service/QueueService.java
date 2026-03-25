package com.smartqueue.service;

import com.smartqueue.dto.QueueDTO;
import com.smartqueue.model.Queue;
import com.smartqueue.model.QueueStatus;
import com.smartqueue.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    @Autowired
    private QueueRepository queueRepository;

    public List<QueueDTO> getQueueStatus() {
        return queueRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QueueDTO updateQueueStatus(Long id, String status) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Queue entry not found."));

        queue.setQueueStatus(QueueStatus.valueOf(status.toUpperCase()));
        
        if (queue.getQueueStatus() == QueueStatus.COMPLETED) {
            queue.getAppointment().setStatus("COMPLETED");
        } else if (queue.getQueueStatus() == QueueStatus.IN_PROGRESS) {
            queue.getAppointment().setStatus("CONFIRMED");
        }
        
        return mapToDTO(queueRepository.save(queue));
    }

    private QueueDTO mapToDTO(Queue queue) {
        return QueueDTO.builder()
                .id(queue.getId())
                .appointmentId(queue.getAppointment().getId())
                .userName(queue.getAppointment().getUser().getName())
                .serviceType(queue.getAppointment().getServiceType())
                .appointmentDate(queue.getAppointment().getAppointmentDate())
                .timeSlot(queue.getAppointment().getTimeSlot())
                .queueNumber(queue.getQueueNumber())
                .queueStatus(queue.getQueueStatus())
                .build();
    }
}
