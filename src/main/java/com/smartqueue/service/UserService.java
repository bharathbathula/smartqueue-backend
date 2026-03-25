package com.smartqueue.service;

import com.smartqueue.model.Appointment;
import com.smartqueue.model.Staff;
import com.smartqueue.model.User;
import com.smartqueue.repository.AppointmentRepository;
import com.smartqueue.repository.QueueRepository;
import com.smartqueue.repository.StaffRepository;
import com.smartqueue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private QueueRepository queueRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        // Find all appointments for this user
        List<Appointment> userAppointments = appointmentRepository.findByUserId(id);
        
        for (Appointment apt : userAppointments) {
            // Delete associated queue entry first
            queueRepository.deleteByAppointmentId(apt.getId());
            // Then delete the appointment
            appointmentRepository.delete(apt);
        }
        
        // Finally delete the user
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteStaff(Long id) {
        // Handle appointments where this staff is the doctor
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorId(id);
        
        for (Appointment apt : doctorAppointments) {
            // Unlink doctor but keep the appointment record
            apt.setDoctor(null);
            appointmentRepository.save(apt);
        }
        
        // Finally delete the staff member
        staffRepository.deleteById(id);
    }
}
