package com.neurosoft.employee_management.attendance.controller;

import com.neurosoft.employee_management.attendance.entity.Attendance;
import com.neurosoft.employee_management.attendance.repository.AttendanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository repository;

    public AttendanceController(AttendanceRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("employeeId");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "employeeId required"));
        }
        
        // Safely convert the ID to a Long (handles both Integer and Long from JSON)
        Long employeeId = ((Number) idObj).longValue();

        if (repository.findOpenCheckIn(employeeId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Employee already checked in"));
        }

        Attendance a = new Attendance();
        a.setEmployeeId(employeeId);
        a.setCheckIn(Instant.now());
        a.setEventType(Attendance.EventType.CHECK_IN);

        // Extract and set GPS coordinates if provided
        Object lat = body.get("latitude");
        Object lng = body.get("longitude");
        if (lat instanceof Number) {
            a.setLatitude(((Number) lat).doubleValue());
        }
        if (lng instanceof Number) {
            a.setLongitude(((Number) lng).doubleValue());
        }

        return ResponseEntity.ok(repository.save(a));
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("employeeId");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "employeeId required"));
        }
        
        Long employeeId = ((Number) idObj).longValue();

        Attendance open = repository.findOpenCheckIn(employeeId).orElse(null);
        if (open == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No open check-in found"));
        }

        open.setCheckOut(Instant.now());
        open.setEventType(Attendance.EventType.CHECK_OUT);
        open.setWorkHours(Duration.between(open.getCheckIn(), open.getCheckOut()).toMinutes() / 60.0);
        return ResponseEntity.ok(repository.save(open));
    }
}