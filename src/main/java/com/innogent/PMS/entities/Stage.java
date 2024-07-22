package com.innogent.PMS.entities;

import com.innogent.PMS.enums.StageName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stageId;
    @Enumerated(EnumType.STRING)
    @Column(length = 50,unique = true, nullable = false)
    private StageName stageName;
}
