package com.rms.rms.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime postedOn;

    private int totalApplications;

    private String companyName;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;
}
