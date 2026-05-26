package com.mindguard.module.appointment.dto;

import com.mindguard.module.appointment.entity.Appointment;
import com.mindguard.module.assessment.dto.AssessmentResultVO;
import com.mindguard.module.emotion.entity.EmotionPost;
import com.mindguard.module.emotion.entity.MoodCheckin;
import com.mindguard.module.user.dto.UserVO;
import lombok.Data;

import java.util.List;

@Data
public class StudentArchiveVO {
    private UserVO user;
    private List<EmotionPost> emotionPosts;
    private List<MoodCheckin> checkins;
    private List<AssessmentResultVO> assessments;
    private List<Appointment> appointments;
}
