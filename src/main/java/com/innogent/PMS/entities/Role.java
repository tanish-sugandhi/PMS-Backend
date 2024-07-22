package com.innogent.PMS.entities;

import com.innogent.PMS.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20,unique = true)
    private RoleName name;
}
