package com.innogent.PMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StageTimeLineDto {
    private Long stageId;
    private Integer timelineId;
    private String stageName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer timelineCycleId;
    private Boolean isActive;
}
