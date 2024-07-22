package com.innogent.PMS.entities;

import com.innogent.PMS.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FeedbackRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    @CreationTimestamp
    private LocalDateTime requestDate;
    private LocalDateTime provideDate;
    private String message;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private User feedbackSeeker;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private User feedbackProvider;
    @Column
    private Integer performanceCycleId;
}