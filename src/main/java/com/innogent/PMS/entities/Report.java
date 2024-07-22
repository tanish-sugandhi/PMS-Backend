package com.innogent.PMS.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Setter
@Getter
public class Report {
    @Id
    @GeneratedValue
    private Long performanceReviewId;
    private Float overallRating;
    private String reviewSummary;
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;


}
