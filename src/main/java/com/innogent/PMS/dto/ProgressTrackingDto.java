package com.innogent.PMS.dto;

import com.innogent.PMS.entities.User;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class ProgressTrackingDto {
    private Long meetingId;
    private Integer userId;
    private Integer managerId;
    @CreationTimestamp
    private LocalDate date;
    private String month;
    private String year;
    private String title;
    private String notes;
    private String recording;
}
