package com.smartqueue.service;

import com.smartqueue.dto.TimeSlotDTO;
import com.smartqueue.model.Role;
import com.smartqueue.model.Staff;
import com.smartqueue.model.TimeSlot;
import com.smartqueue.repository.StaffRepository;
import com.smartqueue.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<TimeSlotDTO> getAllTimeSlots() {
        return timeSlotRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeSlotDTO createTimeSlot(TimeSlotDTO timeSlotDTO, String currentUserEmail) {
        Staff staff = staffRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Error: Staff not found."));

        if (!staff.getRole().equals(Role.STAFF) && !staff.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Error: Unauthorized to create time slots.");
        }

        TimeSlot timeSlot = TimeSlot.builder()
                .slotTime(timeSlotDTO.getSlotTime())
                .maxAppointments(timeSlotDTO.getMaxAppointments())
                .createdBy(staff)
                .build();

        return mapToDTO(timeSlotRepository.save(timeSlot));
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: TimeSlot not found."));
        timeSlotRepository.delete(timeSlot);
    }

    private TimeSlotDTO mapToDTO(TimeSlot timeSlot) {
        return TimeSlotDTO.builder()
                .id(timeSlot.getId())
                .slotTime(timeSlot.getSlotTime())
                .maxAppointments(timeSlot.getMaxAppointments())
                .createdById(timeSlot.getCreatedBy().getId())
                .createdByName(timeSlot.getCreatedBy().getName())
                .build();
    }
}
