package com.mindguard.module.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.common.BusinessException;
import com.mindguard.module.assessment.dto.ScaleVO;
import com.mindguard.module.assessment.entity.Scale;
import com.mindguard.module.assessment.entity.ScaleOption;
import com.mindguard.module.assessment.entity.ScaleQuestion;
import com.mindguard.module.assessment.mapper.ScaleMapper;
import com.mindguard.module.assessment.mapper.ScaleOptionMapper;
import com.mindguard.module.assessment.mapper.ScaleQuestionMapper;
import com.mindguard.module.assessment.service.ScaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScaleServiceImpl implements ScaleService {

    private final ScaleMapper scaleMapper;
    private final ScaleQuestionMapper questionMapper;
    private final ScaleOptionMapper optionMapper;

    @Override
    public List<Scale> listScales() {
        return scaleMapper.selectList(
                new LambdaQueryWrapper<Scale>().eq(Scale::getIsActive, 1));
    }

    @Override
    public ScaleVO getScaleDetail(Long scaleId) {
        Scale scale = scaleMapper.selectById(scaleId);
        if (scale == null) {
            throw new BusinessException("量表不存在");
        }

        ScaleVO vo = new ScaleVO();
        vo.setId(scale.getId());
        vo.setName(scale.getName());
        vo.setDescription(scale.getDescription());
        vo.setType(scale.getType());
        vo.setTotalQuestions(scale.getTotalQuestions());
        vo.setMinScore(scale.getMinScore());
        vo.setMaxScore(scale.getMaxScore());
        vo.setIsActive(scale.getIsActive());
        vo.setCreatedAt(scale.getCreatedAt());

        List<ScaleQuestion> questions = questionMapper.selectList(
                new LambdaQueryWrapper<ScaleQuestion>()
                        .eq(ScaleQuestion::getScaleId, scaleId)
                        .orderByAsc(ScaleQuestion::getQuestionNumber));

        List<ScaleVO.QuestionVO> questionVOs = questions.stream().map(q -> {
            ScaleVO.QuestionVO qvo = new ScaleVO.QuestionVO();
            qvo.setId(q.getId());
            qvo.setQuestionNumber(q.getQuestionNumber());
            qvo.setQuestionText(q.getQuestionText());

            List<ScaleOption> options = optionMapper.selectList(
                    new LambdaQueryWrapper<ScaleOption>()
                            .eq(ScaleOption::getQuestionId, q.getId())
                            .orderByAsc(ScaleOption::getSortOrder));

            List<ScaleVO.OptionVO> optionVOs = options.stream().map(o -> {
                ScaleVO.OptionVO ovo = new ScaleVO.OptionVO();
                ovo.setId(o.getId());
                ovo.setOptionText(o.getOptionText());
                ovo.setScoreValue(o.getScoreValue());
                ovo.setSortOrder(o.getSortOrder());
                return ovo;
            }).collect(Collectors.toList());

            qvo.setOptions(optionVOs);
            return qvo;
        }).collect(Collectors.toList());

        vo.setQuestions(questionVOs);
        return vo;
    }
}
