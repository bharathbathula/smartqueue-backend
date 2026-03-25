package com.smartqueue.controller;

import com.smartqueue.dto.AppointmentDTO;
import com.smartqueue.dto.BookAppointmentRequest;
import com.smartqueue.dto.MessageResponse;
import com.smartqueue.security.UserDetailsImpl;
import com.smartqueue.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(appointmentService.bookAppointment(request, userDetails.getEmail()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AppointmentDTO>> getMyAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(appointmentService.getUserAppointments(userDetails.getEmail()));
    }

    @GetMapping("/me/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<AppointmentDTO>> getMyStaffAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(userDetails.getEmail()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<AppointmentDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, payload.get("status")));
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        appointmentService.cancelAppointment(id, userDetails.getEmail());
        return ResponseEntity.ok(new MessageResponse("Appointment cancelled successfully."));
    }
}
