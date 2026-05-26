package com.mindguard.module.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindguard.common.BusinessException;
import com.mindguard.common.PageResult;
import com.mindguard.module.appointment.dto.*;
import com.mindguard.module.appointment.entity.*;
import com.mindguard.module.appointment.mapper.*;
import com.mindguard.module.appointment.service.AppointmentService;
import com.mindguard.module.assessment.dto.AssessmentResultVO;
import com.mindguard.module.assessment.entity.AssessmentResult;
import com.mindguard.module.assessment.entity.Scale;
import com.mindguard.module.assessment.entity.UserAssessment;
import com.mindguard.module.assessment.mapper.AssessmentResultMapper;
import com.mindguard.module.assessment.mapper.ScaleMapper;
import com.mindguard.module.assessment.mapper.UserAssessmentMapper;
import com.mindguard.module.emotion.entity.Alert;
import com.mindguard.module.emotion.entity.EmotionPost;
import com.mindguard.module.emotion.entity.MoodCheckin;
import com.mindguard.module.emotion.mapper.AlertMapper;
import com.mindguard.module.emotion.mapper.EmotionPostMapper;
import com.mindguard.module.emotion.mapper.MoodCheckinMapper;
import com.mindguard.module.user.dto.UserVO;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final CounselorMapper counselorMapper;
    private final ConsultationRecordMapper consultationRecordMapper;
    private final EvaluationMapper evaluationMapper;
    private final UserMapper userMapper;
    private final AlertMapper alertMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final EmotionPostMapper emotionPostMapper;
    private final MoodCheckinMapper moodCheckinMapper;
    private final ScaleMapper scaleMapper;
    private final UserAssessmentMapper userAssessmentMapper;

    @Override
    @Transactional
    public void createAppointment(Long studentId, AppointmentDTO dto) {
        Counselor counselor = counselorMapper.selectById(dto.getCounselorId());
        if (counselor == null || counselor.getIsAvailable() == 0) {
            throw new BusinessException("咨询师不存在或当前不可约");
        }

        Appointment appointment = new Appointment();
        appointment.setStudentId(studentId);
        appointment.setCounselorId(dto.getCounselorId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setTimeSlot(dto.getTimeSlot());
        appointment.setIssueType(dto.getIssueType());
        appointment.setIsAnonymous(dto.getIsAnonymous() != null && dto.getIsAnonymous() ? 1 : 0);
        appointment.setStatus("PENDING");

        boolean hasAlerts = !alertMapper.selectList(
                new LambdaQueryWrapper<Alert>()
                        .eq(Alert::getUserId, studentId)
                        .eq(Alert::getStatus, "PENDING")).isEmpty();
        boolean hasSevere = !assessmentResultMapper.selectList(
                new LambdaQueryWrapper<AssessmentResult>()
                        .eq(AssessmentResult::getUserId, studentId)
                        .eq(AssessmentResult::getSeverityLevel, "SEVERE")).isEmpty();

        appointment.setPriority(hasAlerts || hasSevere ? 1 : 0);

        appointmentMapper.insert(appointment);
    }

    @Override
    public PageResult<Appointment> getMyAppointments(Long studentId, Integer page, Integer size) {
        Page<Appointment> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getStudentId, studentId)
                .orderByDesc(Appointment::getCreatedAt);
        Page<Appointment> result = appointmentMapper.selectPage(pageObj, wrapper);
        for (Appointment a : result.getRecords()) {
            Counselor counselor = counselorMapper.selectById(a.getCounselorId());
            if (counselor != null) {
                a.setCounselorName(counselor.getRealName());
            }
            Evaluation evaluation = evaluationMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getAppointmentId, a.getId()));
            if (evaluation != null) {
                a.setRating(evaluation.getRating());
                a.setEvaluated(true);
                a.setComment(evaluation.getComment());
            }
            ConsultationRecord record = consultationRecordMapper.selectOne(
                    new LambdaQueryWrapper<ConsultationRecord>().eq(ConsultationRecord::getAppointmentId, a.getId()));
            if (record != null) {
                a.setContentSummary(record.getContentSummary());
                a.setDiagnosis(record.getDiagnosis());
                a.setSuggestions(record.getSuggestions());
            }
        }
        return PageResult.of(result);
    }

    @Override
    public PageResult<Appointment> getCounselorAppointments(Long counselorId, String status,
                                                             Integer page, Integer size) {
        Page<Appointment> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getCounselorId, counselorId)
                .orderByDesc(Appointment::getCreatedAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }
        Page<Appointment> result = appointmentMapper.selectPage(pageObj, wrapper);
        for (Appointment a : result.getRecords()) {
            User student = userMapper.selectById(a.getStudentId());
            if (student != null) {
                a.setStudentName(student.getRealName());
            }
            Counselor counselor = counselorMapper.selectById(a.getCounselorId());
            if (counselor != null) {
                a.setCounselorName(counselor.getRealName());
            }
        }
        return PageResult.of(result);
    }

    @Override
    public Appointment getAppointmentDetail(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        User student = userMapper.selectById(appointment.getStudentId());
        if (student != null) {
            appointment.setStudentName(student.getRealName());
        }
        Counselor counselor = counselorMapper.selectById(appointment.getCounselorId());
        if (counselor != null) {
            appointment.setCounselorName(counselor.getRealName());
        }
        Evaluation evaluation = evaluationMapper.selectOne(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getAppointmentId, id));
        if (evaluation != null) {
            appointment.setRating(evaluation.getRating());
            appointment.setEvaluated(true);
            appointment.setComment(evaluation.getComment());
        }
        ConsultationRecord record = consultationRecordMapper.selectOne(
                new LambdaQueryWrapper<ConsultationRecord>().eq(ConsultationRecord::getAppointmentId, id));
        if (record != null) {
            appointment.setContentSummary(record.getContentSummary());
            appointment.setDiagnosis(record.getDiagnosis());
            appointment.setSuggestions(record.getSuggestions());
        }
        return appointment;
    }

    @Override
    @Transactional
    public void approve(Long appointmentId, Long counselorId) {
        Appointment appointment = validateAppointmentAndCounselor(appointmentId, counselorId);
        if (!"PENDING".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许审批通过");
        }
        appointment.setStatus("APPROVED");
        appointment.setRoomNumber(generateRoomNumber());
        appointmentMapper.updateById(appointment);
    }

    private String generateRoomNumber() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomCode = String.format("%03d", (int)(Math.random() * 1000));
        return "ROOM-" + datePart + "-" + randomCode;
    }

    @Override
    @Transactional
    public void reject(Long appointmentId, Long counselorId, String reason) {
        Appointment appointment = validateAppointmentAndCounselor(appointmentId, counselorId);
        if (!"PENDING".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许拒绝");
        }
        appointment.setStatus("REJECTED");
        appointment.setRejectReason(reason);
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void startConsultation(Long appointmentId, Long counselorId) {
        Appointment appointment = validateAppointmentAndCounselor(appointmentId, counselorId);
        if (!"APPROVED".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许开始咨询");
        }
        appointment.setStatus("IN_PROGRESS");
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void completeConsultation(Long appointmentId, Long counselorId, ConsultationRecordDTO dto) {
        Appointment appointment = validateAppointmentAndCounselor(appointmentId, counselorId);
        if (!"IN_PROGRESS".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许完成咨询");
        }
        appointment.setStatus("COMPLETED");
        appointmentMapper.updateById(appointment);

        ConsultationRecord record = new ConsultationRecord();
        record.setAppointmentId(appointmentId);
        record.setCounselorId(counselorId);
        record.setContentSummary(dto.getContentSummary());
        record.setDiagnosis(dto.getDiagnosis());
        record.setSuggestions(dto.getSuggestions());
        consultationRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void evaluate(Long appointmentId, Long studentId, EvaluationDTO dto) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if (!appointment.getStudentId().equals(studentId)) {
            throw new BusinessException("无权评价他人的预约");
        }
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许评价");
        }

        appointment.setStatus("ARCHIVED");
        appointmentMapper.updateById(appointment);

        Evaluation evaluation = new Evaluation();
        evaluation.setAppointmentId(appointmentId);
        evaluation.setStudentId(studentId);
        evaluation.setRating(dto.getRating());
        evaluation.setComment(dto.getComment());
        evaluationMapper.insert(evaluation);
    }

    @Override
    public StudentArchiveVO getStudentArchive(Long studentId) {
        User user = userMapper.selectById(studentId);
        if (user == null) {
            throw new BusinessException("学生不存在");
        }

        StudentArchiveVO archive = new StudentArchiveVO();

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        archive.setUser(userVO);

        archive.setEmotionPosts(emotionPostMapper.selectList(
                new LambdaQueryWrapper<EmotionPost>()
                        .eq(EmotionPost::getUserId, studentId)
                        .orderByDesc(EmotionPost::getCreatedAt)));

        archive.setCheckins(moodCheckinMapper.selectList(
                new LambdaQueryWrapper<MoodCheckin>()
                        .eq(MoodCheckin::getUserId, studentId)
                        .orderByDesc(MoodCheckin::getCheckinDate)));

        List<AssessmentResult> results = assessmentResultMapper.selectList(
                new LambdaQueryWrapper<AssessmentResult>()
                        .eq(AssessmentResult::getUserId, studentId)
                        .orderByDesc(AssessmentResult::getCreatedAt));

        List<AssessmentResultVO> resultVOs = results.stream().map(r -> {
            AssessmentResultVO vo = new AssessmentResultVO();
            BeanUtils.copyProperties(r, vo);
            Scale scale = scaleMapper.selectById(r.getScaleId());
            if (scale != null) {
                vo.setScaleName(scale.getName());
                vo.setScaleType(scale.getType());
            }
            if (vo.getCreatedAt() == null && r.getAssessmentId() != null) {
                UserAssessment ua = userAssessmentMapper.selectById(r.getAssessmentId());
                if (ua != null && ua.getCompletedAt() != null) {
                    vo.setCreatedAt(ua.getCompletedAt());
                }
            }
            return vo;
        }).collect(Collectors.toList());
        archive.setAssessments(resultVOs);

        List<Appointment> appointments = appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getStudentId, studentId)
                        .orderByDesc(Appointment::getCreatedAt));

        for (Appointment a : appointments) {
            Counselor counselor = counselorMapper.selectById(a.getCounselorId());
            if (counselor != null) {
                a.setCounselorName(counselor.getRealName());
            }
            Evaluation evaluation = evaluationMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getAppointmentId, a.getId()));
            if (evaluation != null) {
                a.setRating(evaluation.getRating());
            }
        }
        archive.setAppointments(appointments);

        List<Long> appointmentIds = appointments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .map(Appointment::getId)
                .collect(Collectors.toList());
        if (!appointmentIds.isEmpty()) {
            archive.setConsultationRecords(consultationRecordMapper.selectList(
                    new LambdaQueryWrapper<ConsultationRecord>()
                            .in(ConsultationRecord::getAppointmentId, appointmentIds)
                            .orderByDesc(ConsultationRecord::getCreatedAt)));
        }

        return archive;
    }

    @Override
    public boolean hasActiveAlerts(Long studentId) {
        return !alertMapper.selectList(
                new LambdaQueryWrapper<Alert>()
                        .eq(Alert::getUserId, studentId)
                        .eq(Alert::getStatus, "PENDING")).isEmpty();
    }

    @Override
    public boolean hasSevereAssessment(Long studentId) {
        return !assessmentResultMapper.selectList(
                new LambdaQueryWrapper<AssessmentResult>()
                        .eq(AssessmentResult::getUserId, studentId)
                        .eq(AssessmentResult::getSeverityLevel, "SEVERE")).isEmpty();
    }

    private Appointment validateAppointmentAndCounselor(Long appointmentId, Long counselorId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if (!appointment.getCounselorId().equals(counselorId)) {
            throw new BusinessException("无权操作非本人的预约");
        }
        return appointment;
    }
}
