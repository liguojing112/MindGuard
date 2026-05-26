package com.mindguard.module.assessment.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.assessment.dto.AssessmentListItemVO;
import com.mindguard.module.assessment.dto.AssessmentResultVO;
import com.mindguard.module.assessment.dto.SubmitAnswerDTO;

import java.util.List;
import java.util.Map;

public interface AssessmentService {
    Long startAssessment(Long userId, Long scaleId);
    AssessmentResultVO submitAnswers(Long assessmentId, Long userId, SubmitAnswerDTO dto);
    PageResult<AssessmentListItemVO> getMyAssessments(Long userId, Integer page, Integer size);
    AssessmentResultVO getAssessmentResult(Long assessmentId, Long userId);
    PageResult<AssessmentResultVO> listAllAssessments(Integer page, Integer size);
    List<Map<String, Object>> getAllAssessmentData();
}
