package com.smartqueue.service;

import com.smartqueue.model.Appointment;
import com.smartqueue.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SmsService smsService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusMinutes(5);

        LocalDate today = targetTime.toLocalDate();
        LocalTime timeAt5Min = targetTime.toLocalTime().withSecond(0).withNano(0);

        List<Appointment> upcomingAppointments = appointmentRepository.findAllByAppointmentDateAndStatus(today, "PENDING");

        for (Appointment app : upcomingAppointments) {
            try {
                String startTimeStr = app.getTimeSlot().split("-")[0].trim();
                LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);

                if (startTime.equals(timeAt5Min)) {
                    sendNotifications(app);
                }
            } catch (Exception e) {
                System.err.println("Error parsing time slot for appointment " + app.getId() + ": " + e.getMessage());
            }
        }
    }

    private void sendNotifications(Appointment app) {
        String msg = "Hi " + app.getUser().getName() + ", your medical consultation for " + app.getServiceType() + 
                     " is scheduled in 5 minutes (" + app.getTimeSlot() + "). Please be ready at the OPD counter. - MediQueue Hospital";
        
        if (app.getUser().getPhone() != null) {
            smsService.sendSms(app.getUser().getPhone(), msg);
        }
    }
}
