package com.innogent.PMS.entities;

import com.innogent.PMS.enums.EvaluationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type")
    private EvaluationType feedbackType;
    private Integer rating;
    private LocalDateTime feedbackDate;
    @Column(columnDefinition = "TEXT")
    private String comments;
    @Column
    private Integer performanceCycleId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "provider_id")
    private User provider;

}
