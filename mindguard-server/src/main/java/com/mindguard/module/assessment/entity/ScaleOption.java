package com.mindguard.module.assessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("scale_option")
public class ScaleOption {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private String optionText;
    private Integer scoreValue;
    private Integer sortOrder;
}
