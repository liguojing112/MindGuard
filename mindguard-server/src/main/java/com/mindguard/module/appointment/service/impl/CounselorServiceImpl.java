package com.mindguard.module.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.common.BusinessException;
import com.mindguard.module.appointment.dto.CounselorVO;
import com.mindguard.module.appointment.entity.Appointment;
import com.mindguard.module.appointment.entity.Counselor;
import com.mindguard.module.appointment.mapper.AppointmentMapper;
import com.mindguard.module.appointment.mapper.CounselorMapper;
import com.mindguard.module.appointment.service.CounselorService;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselorServiceImpl implements CounselorService {

    private final CounselorMapper counselorMapper;
    private final AppointmentMapper appointmentMapper;
    private final UserMapper userMapper;

    private static final List<String> ALL_SLOTS = List.of(
            "09:00-09:50", "10:00-10:50", "11:00-11:50",
            "14:00-14:50", "15:00-15:50", "16:00-16:50",
            "19:00-19:50", "20:00-20:50"
    );

    @Override
    public List<CounselorVO> listCounselors() {
        List<Counselor> counselors = counselorMapper.selectList(
                new LambdaQueryWrapper<Counselor>().eq(Counselor::getIsAvailable, 1));
        return counselors.stream().map(this::buildCounselorVO).collect(Collectors.toList());
    }

    @Override
    public CounselorVO getCounselorDetail(Long id) {
        Counselor counselor = counselorMapper.selectById(id);
        if (counselor == null) {
            throw new BusinessException("咨询师不存在");
        }
        return buildCounselorVO(counselor);
    }

    @Override
    public List<String> getAvailableSlots(Long counselorId, String date) {
        Counselor counselor = counselorMapper.selectById(counselorId);
        if (counselor == null) {
            throw new BusinessException("咨询师不存在");
        }

        List<Appointment> booked = appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getCounselorId, counselorId)
                        .eq(Appointment::getAppointmentDate, java.time.LocalDate.parse(date))
                        .notIn(Appointment::getStatus, "REJECTED", "ARCHIVED"));

        Set<String> bookedSlots = booked.stream()
                .map(Appointment::getTimeSlot)
                .collect(Collectors.toSet());

        List<String> available = new ArrayList<>();
        for (String slot : ALL_SLOTS) {
            if (!bookedSlots.contains(slot)) {
                available.add(slot);
            }
        }
        return available;
    }

    private CounselorVO buildCounselorVO(Counselor counselor) {
        CounselorVO vo = new CounselorVO();
        vo.setId(counselor.getId());
        vo.setUserId(counselor.getUserId());
        vo.setRealName(counselor.getRealName());
        vo.setTitle(counselor.getTitle());
        vo.setSpecialty(counselor.getSpecialty());
        vo.setDescription(counselor.getDescription());
        vo.setAvatar(counselor.getAvatar());
        vo.setIsAvailable(counselor.getIsAvailable());
        vo.setCreatedAt(counselor.getCreatedAt());

        User user = userMapper.selectById(counselor.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setEmail(user.getEmail());
        }

        return vo;
    }
}
