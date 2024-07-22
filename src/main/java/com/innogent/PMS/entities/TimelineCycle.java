package com.innogent.PMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "timeline_cycles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TimelineCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer timelineCycleId;
    private LocalDate startDate;
    private LocalDate endDate;
}