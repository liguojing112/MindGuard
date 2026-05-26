package com.mindguard.module.appointment.service;

import com.mindguard.module.appointment.dto.CounselorVO;

import java.util.List;

public interface CounselorService {
    List<CounselorVO> listCounselors();
    CounselorVO getCounselorDetail(Long id);
    List<String> getAvailableSlots(Long counselorId, String date);
}
