package com.mindguard.module.assessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("scale_question")
public class ScaleQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scaleId;
    private Integer questionNumber;
    private String questionText;
}
