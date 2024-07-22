package com.innogent.PMS.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "progress_trackings")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProgressTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long meetingId;
    @CreationTimestamp

   private LocalDate date;
   private String month;
   private String year;
   @Column(length = 100000)
   private String notes;
   @Column(length = 100000)
   private String recording;
   private String title;

  @ManyToOne
  @JoinColumn(name="empId")
   private User user;

    private Integer lineManagerId;
}
