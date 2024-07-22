package com.innogent.PMS.dto;

import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class FeedbackRequestDto {
    private Long requestId;
    private RequestStatus requestStatus;
    private LocalDateTime requestDate;
    private LocalDateTime provideDate;
    private String message;
    private Integer feedbackSeekerId;
    private Integer feedbackProviderId;
    private String feedbackSeekerName;
    private String feedbackProviderName;
    private String feedbackProviderEmail;
    private Integer performanceCycleId;
}
