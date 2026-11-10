package com.neurosoft.employee_management.attendance.repository;

import com.neurosoft.employee_management.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId " +
           "AND a.checkOut IS NULL ORDER BY a.checkIn DESC")
    Optional<Attendance> findOpenCheckIn(@Param("employeeId") Long employeeId);
}
