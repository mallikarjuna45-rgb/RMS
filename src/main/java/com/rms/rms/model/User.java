package com.rms.rms.model;

import com.rms.rms.utils.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email
    @Column(unique = true)
    private String email;

    private String address;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String password;

    private String profileHeadline;

    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    private List<Job> jobsPosted;
}

