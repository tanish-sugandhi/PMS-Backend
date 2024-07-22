package com.innogent.PMS.dto;

import com.innogent.PMS.enums.EvaluationType;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class    FeedbackDto {
    private Integer feedbackId;
    private Integer userId;
    private Integer providerId;
    private String providerName;
    private EvaluationType feedbackType;
    private Integer rating;
    private LocalDateTime feedbackDate;
    private String comments;
    private Integer performanceCycleId;
}
