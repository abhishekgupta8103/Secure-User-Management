package com.example.demo.controller;

import com.example.demo.entity.Appointment;
import com.example.demo.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Appointment> createAppointment(
            @PathVariable Long userId,
            @RequestBody Appointment appointment) {

        Appointment savedAppointment =
                appointmentService.createAppointment(
                        userId,
                        appointment
                );

        return ResponseEntity.ok(savedAppointment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getUserAppointments(
            @PathVariable Long userId) {

        List<Appointment> appointments =
                appointmentService.getUserAppointments(userId);

        return ResponseEntity.ok(appointments);
    }
}