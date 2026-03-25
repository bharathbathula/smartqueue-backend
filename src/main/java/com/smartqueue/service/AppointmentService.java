package com.smartqueue.service;

import com.smartqueue.dto.AppointmentDTO;
import com.smartqueue.dto.BookAppointmentRequest;
import com.smartqueue.model.*;
import com.smartqueue.repository.AppointmentRepository;
import com.smartqueue.repository.QueueRepository;
import com.smartqueue.repository.StaffRepository;
import com.smartqueue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SmsService smsService;

    @Transactional
    public AppointmentDTO bookAppointment(BookAppointmentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        Appointment.AppointmentBuilder appointmentBuilder = Appointment.builder()
                .user(user)
                .serviceType(request.getServiceType())
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .status("PENDING");

        if (request.getDoctorId() != null) {
            Staff doctor = staffRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Error: Doctor not found."));
            appointmentBuilder.doctor(doctor);
        }

        Appointment appointment = appointmentBuilder.build();
        appointment = appointmentRepository.save(appointment);

        // Auto-generate Queue number
        Queue lastQueue = queueRepository.findTopByOrderByQueueNumberDesc();
        int nextQueueNumber = (lastQueue != null) ? lastQueue.getQueueNumber() + 1 : 1;

        Queue queue = Queue.builder()
                .appointment(appointment)
                .queueNumber(nextQueueNumber)
                .queueStatus(QueueStatus.WAITING)
                .build();

        queueRepository.save(queue);

        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getUserAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return appointmentRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getDoctorAppointments(String email) {
        Staff staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: Staff not found."));
        
        return appointmentRepository.findByDoctorId(staff.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Appointment not found."));

        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);

        if ("COMPLETED".equalsIgnoreCase(status)) {
            sendThankYouNotification(saved);
        }

        return mapToDTO(saved);
    }

    private void sendThankYouNotification(Appointment app) {
        String msg = "Thank you " + app.getUser().getName() + " for visiting MediQueue Hospital for your " + 
                     app.getServiceType() + " consultation. We wish you a speedy recovery!";
        if (app.getUser().getPhone() != null) {
            smsService.sendSms(app.getUser().getPhone(), msg);
        }
    }

    @Transactional
    public void cancelAppointment(Long id, String userEmail) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Appointment not found."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        if (!appointment.getUser().getId().equals(user.getId())) {
             throw new RuntimeException("Error: Unauthorized to cancel this appointment.");
        }

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
        
        // Optionally update queue status if cancelled
        queueRepository.findByAppointmentId(id).ifPresent(queue -> {
            queue.setQueueStatus(QueueStatus.COMPLETED); // Or a specific CANCELLED status
            queueRepository.save(queue);
        });
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO.AppointmentDTOBuilder builder = AppointmentDTO.builder()
                .id(appointment.getId())
                .userId(appointment.getUser().getId())
                .userName(appointment.getUser().getName())
                .serviceType(appointment.getServiceType())
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlot(appointment.getTimeSlot())
                .status(appointment.getStatus());

        if (appointment.getDoctor() != null) {
            builder.doctorId(appointment.getDoctor().getId())
                   .doctorName(appointment.getDoctor().getName())
                   .doctorSpecialty(appointment.getDoctor().getSpecialty());
        }

        return builder.build();
    }
}
