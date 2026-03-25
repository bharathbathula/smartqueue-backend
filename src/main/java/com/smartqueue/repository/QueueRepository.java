package com.smartqueue.repository;

import com.smartqueue.model.Queue;
import com.smartqueue.model.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByAppointmentId(Long appointmentId);
    void deleteByAppointmentId(Long appointmentId);
    List<Queue> findByQueueStatus(QueueStatus queueStatus);
    Queue findTopByOrderByQueueNumberDesc();
}
