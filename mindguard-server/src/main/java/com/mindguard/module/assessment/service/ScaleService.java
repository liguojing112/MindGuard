package com.mindguard.module.assessment.service;

import com.mindguard.module.assessment.dto.ScaleVO;
import com.mindguard.module.assessment.entity.Scale;

import java.util.List;

public interface ScaleService {
    List<Scale> listScales();
    ScaleVO getScaleDetail(Long scaleId);
}
