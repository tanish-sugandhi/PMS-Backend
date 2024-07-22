package com.innogent.PMS.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    @Column(length = 100, unique = true)
    private String email;
    @Column(length = 100)
    private String password;
    @Column(length = 15)
    private String contact;
    @Column(length = 50)
    private String job;
    private Date hiredDate;
    private Integer managerId;
    private String aboutMe;

    @ManyToOne(cascade= CascadeType.MERGE, targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.isDeleted = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
