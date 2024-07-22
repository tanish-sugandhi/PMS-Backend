package com.innogent.PMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "timelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Timeline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer timelineId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String description;
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
        }
    }

    @Column(name = "stage_id")
    private Long stageId;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "timeline_cycle_id", referencedColumnName = "timelineCycleId")
    private TimelineCycle timelineCycle;
}
