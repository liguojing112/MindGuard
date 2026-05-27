package com.mindguard.module.appointment.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.appointment.dto.AppointmentDTO;
import com.mindguard.module.appointment.dto.ConsultationRecordDTO;
import com.mindguard.module.appointment.dto.EvaluationDTO;
import com.mindguard.module.appointment.dto.StudentArchiveVO;
import com.mindguard.module.appointment.entity.Appointment;

import java.util.Map;

public interface AppointmentService {
    void createAppointment(Long studentId, AppointmentDTO dto);
    PageResult<Appointment> getMyAppointments(Long studentId, Integer page, Integer size);
    PageResult<Appointment> getCounselorAppointments(Long counselorId, String status, Integer page, Integer size);
    Appointment getAppointmentDetail(Long id);
    void approve(Long appointmentId, Long counselorId);
    void reject(Long appointmentId, Long counselorId, String reason);
    void startConsultation(Long appointmentId, Long counselorId);
    void completeConsultation(Long appointmentId, Long counselorId, ConsultationRecordDTO dto);
    void evaluate(Long appointmentId, Long studentId, EvaluationDTO dto);
    StudentArchiveVO getStudentArchive(Long studentId);
    boolean hasActiveAlerts(Long studentId);
    boolean hasSevereAssessment(Long studentId);
    Map<String, String> generateAISuggestion(Long appointmentId);
}
