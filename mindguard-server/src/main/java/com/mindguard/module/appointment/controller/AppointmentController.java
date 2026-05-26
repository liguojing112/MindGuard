package com.mindguard.module.appointment.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.appointment.dto.*;
import com.mindguard.module.appointment.entity.Appointment;
import com.mindguard.module.appointment.service.AppointmentService;
import com.mindguard.module.appointment.service.CounselorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final CounselorService counselorService;
    private final AppointmentService appointmentService;

    @GetMapping("/appointments/counselors")
    public Result<List<CounselorVO>> listCounselors() {
        return Result.ok(counselorService.listCounselors());
    }

    @GetMapping("/appointments/counselors/{id}")
    public Result<CounselorVO> getCounselorDetail(@PathVariable Long id) {
        return Result.ok(counselorService.getCounselorDetail(id));
    }

    @GetMapping("/appointments/counselors/{id}/slots")
    public Result<List<String>> getAvailableSlots(@PathVariable Long id, @RequestParam String date) {
        return Result.ok(counselorService.getAvailableSlots(id, date));
    }

    @PostMapping("/appointments")
    public Result<Void> createAppointment(HttpServletRequest request, @RequestBody AppointmentDTO dto) {
        Long studentId = (Long) request.getAttribute("userId");
        appointmentService.createAppointment(studentId, dto);
        return Result.ok();
    }

    @GetMapping("/appointments/my")
    public Result<PageResult<Appointment>> getMyAppointments(HttpServletRequest request,
                                                              @RequestParam(defaultValue = "1") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = (Long) request.getAttribute("userId");
        return Result.ok(appointmentService.getMyAppointments(studentId, page, size));
    }

    @GetMapping("/appointments/counselor")
    public Result<PageResult<Appointment>> getCounselorAppointments(HttpServletRequest request,
                                                                     @RequestParam(required = false) String status,
                                                                     @RequestParam(defaultValue = "1") Integer page,
                                                                     @RequestParam(defaultValue = "10") Integer size) {
        Long counselorId = (Long) request.getAttribute("userId");
        return Result.ok(appointmentService.getCounselorAppointments(counselorId, status, page, size));
    }

    @GetMapping("/appointments/{id}")
    public Result<Appointment> getAppointmentDetail(@PathVariable Long id) {
        return Result.ok(appointmentService.getAppointmentDetail(id));
    }

    @PutMapping("/appointments/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, HttpServletRequest request) {
        Long counselorId = (Long) request.getAttribute("userId");
        appointmentService.approve(id, counselorId);
        return Result.ok();
    }

    @PutMapping("/appointments/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> body,
                                HttpServletRequest request) {
        Long counselorId = (Long) request.getAttribute("userId");
        String reason = body.get("reason");
        appointmentService.reject(id, counselorId, reason);
        return Result.ok();
    }

    @PutMapping("/appointments/{id}/start")
    public Result<Void> startConsultation(@PathVariable Long id, HttpServletRequest request) {
        Long counselorId = (Long) request.getAttribute("userId");
        appointmentService.startConsultation(id, counselorId);
        return Result.ok();
    }

    @PostMapping("/appointments/{id}/complete")
    public Result<Void> completeConsultation(@PathVariable Long id, HttpServletRequest request,
                                              @RequestBody ConsultationRecordDTO dto) {
        Long counselorId = (Long) request.getAttribute("userId");
        appointmentService.completeConsultation(id, counselorId, dto);
        return Result.ok();
    }

    @PostMapping("/appointments/{id}/evaluate")
    public Result<Void> evaluate(@PathVariable Long id, HttpServletRequest request,
                                  @RequestBody EvaluationDTO dto) {
        Long studentId = (Long) request.getAttribute("userId");
        appointmentService.evaluate(id, studentId, dto);
        return Result.ok();
    }

    @GetMapping("/students/{id}/archive")
    public Result<StudentArchiveVO> getStudentArchive(@PathVariable Long id) {
        return Result.ok(appointmentService.getStudentArchive(id));
    }
}
