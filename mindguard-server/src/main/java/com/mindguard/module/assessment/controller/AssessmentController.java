package com.mindguard.module.assessment.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.assessment.dto.AssessmentListItemVO;
import com.mindguard.module.assessment.dto.AssessmentResultVO;
import com.mindguard.module.assessment.dto.ScaleVO;
import com.mindguard.module.assessment.dto.StartAssessmentDTO;
import com.mindguard.module.assessment.dto.SubmitAnswerDTO;
import com.mindguard.module.assessment.entity.Scale;
import com.mindguard.module.assessment.service.AssessmentService;
import com.mindguard.module.assessment.service.ScaleService;
import com.mindguard.util.ExcelExportUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final ScaleService scaleService;
    private final AssessmentService assessmentService;

    @GetMapping("/scales")
    public Result<List<Scale>> listScales() {
        return Result.ok(scaleService.listScales());
    }

    @GetMapping("/scales/{id}")
    public Result<ScaleVO> getScaleDetail(@PathVariable Long id) {
        return Result.ok(scaleService.getScaleDetail(id));
    }

    @PostMapping("/start")
    public Result<Map<String, Long>> startAssessment(HttpServletRequest request,
                                                      @RequestBody StartAssessmentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        Long assessmentId = assessmentService.startAssessment(userId, dto.getScaleId());
        Map<String, Long> data = new HashMap<>();
        data.put("assessmentId", assessmentId);
        return Result.ok(data);
    }

    @PostMapping("/{id}/submit")
    public Result<AssessmentResultVO> submitAnswers(@PathVariable Long id,
                                                     HttpServletRequest request,
                                                     @RequestBody SubmitAnswerDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(assessmentService.submitAnswers(id, userId, dto));
    }

    @GetMapping("/my")
    public Result<PageResult<AssessmentListItemVO>> getMyAssessments(HttpServletRequest request,
                                                                     @RequestParam(defaultValue = "1") Integer page,
                                                                     @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(assessmentService.getMyAssessments(userId, page, size));
    }

    @GetMapping("/{id}/result")
    public Result<AssessmentResultVO> getAssessmentResult(@PathVariable Long id,
                                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(assessmentService.getAssessmentResult(id, userId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "测评统计通过Dashboard接口获取");
        return Result.ok(stats);
    }

    @GetMapping("/all")
    public Result<PageResult<AssessmentResultVO>> listAllAssessments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(assessmentService.listAllAssessments(page, size));
    }

    @GetMapping("/export")
    public void exportAssessments(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> data = assessmentService.getAllAssessmentData();
        ExcelExportUtil.exportAssessmentStats(data, response);
    }
}
